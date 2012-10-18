package libshapedraw.internal;

import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import libshapedraw.ApiInfo;
import libshapedraw.LibShapeDraw;
import libshapedraw.MinecraftAccess;
import libshapedraw.animation.trident.TridentConfig;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.internal.LSDUtil.FileLogger;
import libshapedraw.internal.LSDUtil.NullLogger;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.shape.Shape;

import org.lwjgl.opengl.GL11;

/**
 * Internal singleton controller class, lazily instantiated.
 * Relies on a bootstrapper (mod_LibShapeDraw) to feed it Minecraft game events.
 */
public class LSDController {
    private static LSDController instance;
    private final Logger log;
    private final LinkedHashSet<LibShapeDraw> apiInstances;
    private int topApiInstanceId;
    private MinecraftAccess minecraftAccess;
    private boolean initialized;
    private long lastDump;
    /**
     * empty: need update check
     * non-empty: new version available; need to send notification
     * null: no update check needed (version is current, update check disabled,
     *       update check failed, or new version notification sent)
     */
    private String updateCheckResult = "";

    private LSDController() {
        if (LSDGlobalSettings.isLoggingEnabled()) {
            log = new FileLogger(LSDModDirectory.DIRECTORY, ApiInfo.getName(), LSDGlobalSettings.isLoggingAppend());
        } else {
            log = new NullLogger();
        }
        apiInstances = new LinkedHashSet<LibShapeDraw>();
        topApiInstanceId = 0;

        TridentConfig trident = TridentConfig.getInstance();
        trident.addPropertyInterpolator(new ReadonlyColorPropertyInterpolator());
        trident.addPropertyInterpolator(new ReadonlyVector3PropertyInterpolator());
        trident.addPropertyInterpolator(new ReadonlyLineStylePropertyInterpolator());

        log.info(ApiInfo.getName() + " v" + ApiInfo.getVersion() + " by " + ApiInfo.getAuthors());
        log.info(ApiInfo.getUrlMain().toString());
        log.info(ApiInfo.getUrlSource().toString());
        log.info(getClass().getName() + " instantiated");
    }

    public static LSDController getInstance() {
        if (instance == null) {
            instance = new LSDController();
        }
        return instance;
    }

    public static Logger getLog() {
        return getInstance().log;
    }

    public static MinecraftAccess getMinecraftAccess() {
        return getInstance().minecraftAccess;
    }

    /**
     * @return true if the bootstrapper has been instantiated and is linked up to the controller
     */
    public static boolean isInitialized() {
        return getInstance().initialized;
    }

    /**
     * Called by the bootstrapper.
     */
    public void initialize(MinecraftAccess minecraftAccess) {
        if (isInitialized()) {
            throw new IllegalStateException("multiple initializations of controller");
        }
        this.minecraftAccess = minecraftAccess;
        initialized = true;
        log.info(getClass().getName() + " initialized");
    }

    /**
     * Called by LibShapeDraw's constructor.
     */
    public String registerApiInstance(LibShapeDraw apiInstance, String ownerId) {
        if (apiInstances.contains(apiInstance)) {
            throw new IllegalStateException("already registered");
        }
        topApiInstanceId++;
        String apiInstanceId = apiInstance.getClass().getSimpleName() + "#" + topApiInstanceId + ":" + ownerId;
        apiInstances.add(apiInstance);
        log.info("registered API instance " + apiInstanceId);
        return apiInstanceId;
    }

    /**
     * Called by LibShapeDraw.unregister.
     */
    public boolean unregisterApiInstance(LibShapeDraw apiInstance) {
        boolean result = apiInstances.remove(apiInstance);
        if (result) {
            log.info("unregistered API instance " + apiInstance.getInstanceId());
        }
        return result;
    }

    /**
     * Called by the bootstrapper.
     * Dispatch the respawn event.
     */
    public void respawn(ReadonlyVector3 playerCoords, boolean isNewServer, boolean isNewDimension) {
        log.finer("respawn");

        // Dispatch respawn event.
        for (LibShapeDraw apiInstance : apiInstances) {
            if (!apiInstance.getEventListeners().isEmpty()) {
                LSDRespawnEvent event = new LSDRespawnEvent(apiInstance, playerCoords, isNewServer, isNewDimension);
                for (LSDEventListener listener : apiInstance.getEventListeners()) {
                    listener.onRespawn(event);
                }
            }
        }

        // Wait until respawn to start the update check because we don't have a
        // standard method of notifying the user until the chat GUI exists.
        startUpdateCheck();
    }

    /**
     * Called by the bootstrapper.
     * Periodically dump API state to log if configured to do so.
     * Dispatch gameTick events.
     */
    public void gameTick(ReadonlyVector3 playerCoords) {
        log.finer("gameTick");

        // Debug dump.
        if (LSDGlobalSettings.getLoggingDebugDumpInterval() > 0) {
            long now = System.currentTimeMillis();
            if (now > lastDump + LSDGlobalSettings.getLoggingDebugDumpInterval()) {
                dump();
                lastDump = now;
            }
        }

        // Dispatch game tick event.
        for (LibShapeDraw apiInstance : apiInstances) {
            if (!apiInstance.getEventListeners().isEmpty()) {
                LSDGameTickEvent event = new LSDGameTickEvent(apiInstance, playerCoords);
                for (LSDEventListener listener : apiInstance.getEventListeners()) {
                    if (listener != null) {
                        listener.onGameTick(event);
                    }
                }
            }
        }

        // Output results of update check.
        if (updateCheckResult != null && !updateCheckResult.isEmpty()) {
            for (String line : updateCheckResult.split("\n")) {
                minecraftAccess.sendChatMessage(line);
            }
            updateCheckResult = null;
        }
    }

    /**
     * Called by the bootstrapper.
     * Dispatch preRender events.
     * Render all registered shapes.
     */
    public void render(ReadonlyVector3 playerCoords, boolean isGuiHidden) {
        log.finer("render");

        // Initialize OpenGL for our rendering.
        int origDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        GL11.glPushMatrix();
        GL11.glTranslated(-playerCoords.getX(), -playerCoords.getY(), -playerCoords.getZ());

        // Dispatch prerender event and render.
        for (LibShapeDraw apiInstance : apiInstances) {
            if (!apiInstance.getEventListeners().isEmpty()) {
                LSDPreRenderEvent event = new LSDPreRenderEvent(apiInstance, playerCoords, minecraftAccess.getPartialTick(), isGuiHidden);
                for (LSDEventListener listener : apiInstance.getEventListeners()) {
                    if (listener != null) {
                        listener.onPreRender(event);
                    }
                }
            }
            if (apiInstance.isVisible() && (!isGuiHidden || apiInstance.isVisibleWhenHidingGui())) {
                for (Shape shape : apiInstance.getShapes()) {
                    if (shape != null) {
                        shape.render(minecraftAccess);
                    }
                }
            }
        }

        // Revert OpenGL settings so we don't impact any elements Minecraft has
        // left to render.
        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDepthFunc(origDepthFunc);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
    }

    /**
     * Log all the things.
     */
    public boolean dump() {
        if (!log.isLoggable(Level.INFO)) {
            return false;
        }
        final String INDENT = "    ";
        StringBuilder line = new StringBuilder().append(this).append(":\n");
        for (LibShapeDraw apiInstance : apiInstances) {
            line.append(INDENT).append(apiInstance).append(":\n");

            line.append(INDENT).append(INDENT).append("visible=");
            line.append(apiInstance.isVisible()).append('\n');

            line.append(INDENT).append(INDENT).append("visibleWhenHidingGui=");
            line.append(apiInstance.isVisibleWhenHidingGui()).append('\n');

            line.append(INDENT).append(INDENT).append("shapes=");
            line.append(apiInstance.getShapes().size()).append(":\n");
            for (Shape shape : apiInstance.getShapes()) {
                line.append(INDENT).append(INDENT).append(INDENT).append(shape).append('\n');
            }

            line.append(INDENT).append(INDENT).append("eventListeners=");
            line.append(apiInstance.getEventListeners().size()).append(":\n");
            for (LSDEventListener listener : apiInstance.getEventListeners()) {
                line.append(INDENT).append(INDENT).append(INDENT).append(listener).append('\n');
            }
        }
        log.info(line.toString());
        return true;
    }

    private void startUpdateCheck() {
        if (updateCheckResult == null || !updateCheckResult.isEmpty()) {
            return;
        }
        updateCheckResult = null;
        if (!LSDGlobalSettings.isUpdateCheckEnabled()) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                log.info("update check request: " + ApiInfo.getUrlUpdate());
                String response = LSDUtil.getUrlContents(ApiInfo.getUrlUpdate());
                log.info("update check response: " + String.valueOf(response));
                if (response == null) {
                    return;
                }
                // Parse response and set updateCheckResult, which will be
                // consumed and output later in the main thread.
                String[] lines = response.replaceAll("\t", "  ").split("\n");
                if (lines[0].startsWith("{")) {
                    // In case we ever want to switch to JSON in the future
                    updateCheckResult = buildOutput("");
                    return;
                }
                // The first line is simply the latest published version.
                if (ApiInfo.isVersionAtLeast(lines[0])) {
                    return;
                }
                // If the response contains lines of text after the version,
                // that's what we'll output to the user.
                StringBuilder b = new StringBuilder();
                for (int i = 1; i < lines.length; i++) {
                    if (!lines[i].isEmpty()) {
                        b.append(lines[i]).append('\n');
                    }
                }
                if (b.length() > 0) {
                    updateCheckResult = b.toString();
                } else {
                    // The response was just the version.
                    updateCheckResult = buildOutput(lines[0]);
                }
            }
            private String buildOutput(String newVersion) {
                return new StringBuilder().append("\u00a7c")
                        .append(ApiInfo.getName()).append(" is out of date. ")
                        .append(newVersion.isEmpty() ? "A new version" : "Version ")
                        .append(newVersion)
                        .append(" is available at\n  \u00a7c")
                        .append(ApiInfo.getUrlShort()).toString();
            }
        }).start();
    }
}
