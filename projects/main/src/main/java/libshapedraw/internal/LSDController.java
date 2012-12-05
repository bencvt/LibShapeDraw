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
 * Relies on mod_LibShapeDraw to feed it Minecraft game events.
 */
public class LSDController {
    private static LSDController instance;
    private final Logger log;
    private final LinkedHashSet<LibShapeDraw> apiInstances;
    private int topApiInstanceId;
    private MinecraftAccess minecraftAccess;
    private LSDUpdateCheck updateCheck;
    private boolean initialized;
    private long lastDump;

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
     * @return true if mod_LibShapeDraw has been instantiated and is linked up
     *         to the controller
     */
    public static boolean isInitialized() {
        return getInstance().initialized;
    }

    /**
     * Called by mod_LibShapeDraw to establish a link between the LSDController
     * and mod_LibShapeDraw singletons.
     */
    public void initialize(MinecraftAccess minecraftAccess) {
        if (isInitialized()) {
            // LibShapeDraw is probably installed incorrectly, causing
            // ModLoader/Forge to bogusly instantiate mod_LibShapeDraw multiple
            // times.
            throw new IllegalStateException("multiple initializations of controller");
        }
        this.minecraftAccess = minecraftAccess;
        initialized = true;
        log.info(getClass().getName() + " initialized by " + minecraftAccess.getClass().getName());
    }

    /**
     * Called by LibShapeDraw's constructor.
     */
    public String registerApiInstance(LibShapeDraw apiInstance, String ownerId) {
        if (apiInstances.contains(apiInstance)) {
            throw new IllegalStateException("already registered");
        }
        String apiInstanceId = apiInstance.getClass().getSimpleName() + "#" + topApiInstanceId;
        topApiInstanceId++;
        apiInstances.add(apiInstance);
        log.info("registered API instance " + apiInstanceId + ":" + ownerId);
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
     * Called by mod_LibShapeDraw.
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
    }

    /**
     * Called by mod_LibShapeDraw.
     * Periodically dump API state to log if configured to do so.
     * Dispatch gameTick events.
     * Handle update check.
     */
    public void gameTick(ReadonlyVector3 playerCoords) {
        log.finer("gameTick");

        // Debug dump.
        if (LSDGlobalSettings.getLoggingDebugDumpInterval() > 0) {
            long now = System.currentTimeMillis();
            if (now > lastDump + LSDGlobalSettings.getLoggingDebugDumpInterval()) {
                debugDump();
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

        if (updateCheck == null) {
            // Launch a new thread to send an update check through the network.
            // Will only happen once per Minecraft session.
            //
            // We waited until the first game tick event to start the check
            // because we didn't have a place to notify the user until now.
            updateCheck = new LSDUpdateCheck();
        } else {
            updateCheck.announceResultIfReady(minecraftAccess);
        }
    }

    /**
     * Called by mod_LibShapeDraw.
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
            minecraftAccess.profilerStartSection(apiInstance.getInstanceId()).profilerStartSection("prerender");
            if (!apiInstance.getEventListeners().isEmpty()) {
                LSDPreRenderEvent event = new LSDPreRenderEvent(apiInstance, playerCoords, minecraftAccess.getPartialTick(), isGuiHidden);
                for (LSDEventListener listener : apiInstance.getEventListeners()) {
                    if (listener != null) {
                        listener.onPreRender(event);
                    }
                }
            }
            minecraftAccess.profilerEndStartSection("render");
            if (apiInstance.isVisible() && (!isGuiHidden || apiInstance.isVisibleWhenHidingGui())) {
                for (Shape shape : apiInstance.getShapes()) {
                    if (shape != null) {
                        shape.render(minecraftAccess);
                    }
                }
            }
            minecraftAccess.profilerEndSection().profilerEndSection();
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
    public boolean debugDump() {
        if (!log.isLoggable(Level.INFO)) {
            return false;
        }
        final String INDENT = "    ";
        StringBuilder line = new StringBuilder().append("debug dump for ").append(this).append(":\n");
        for (LibShapeDraw apiInstance : apiInstances) {
            line.append(INDENT).append(apiInstance.getInstanceId()).append(":\n");

            line.append(INDENT).append(INDENT).append("id=");
            line.append(apiInstance).append('\n');

            line.append(INDENT).append(INDENT).append("visible=");
            line.append(apiInstance.isVisible()).append('\n');

            line.append(INDENT).append(INDENT).append("visibleWhenHidingGui=");
            line.append(apiInstance.isVisibleWhenHidingGui()).append('\n');

            line.append(INDENT).append(INDENT).append("shapes=");
            if (apiInstance.getShapes().size() == 0) {
                line.append("0\n");
            } else {
                line.append(apiInstance.getShapes().size()).append(":\n");
                for (Shape shape : apiInstance.getShapes()) {
                    line.append(INDENT).append(INDENT).append(INDENT).append(shape).append('\n');
                }
            }

            line.append(INDENT).append(INDENT).append("eventListeners=");
            if (apiInstance.getEventListeners().size() == 0) {
                line.append("0\n");
            } else {
                line.append(apiInstance.getEventListeners().size()).append(":\n");
                for (LSDEventListener listener : apiInstance.getEventListeners()) {
                    line.append(INDENT).append(INDENT).append(INDENT).append(listener).append('\n');
                }
            }
        }
        log.info(line.toString());
        return true;
    }
}
