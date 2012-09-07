package libshapedraw.internal;

import java.util.LinkedHashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import libshapedraw.ApiInfo;
import libshapedraw.LibShapeDraw;
import libshapedraw.MinecraftAccess;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.internal.Util.FileLogger;
import libshapedraw.internal.Util.NullLogger;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.shape.Shape;

import org.lwjgl.opengl.GL11;

/**
 * Internal singleton controller class, lazily instantiated.
 * Relies on a bootstrapper (mod_LibShapeDraw) to feed it Minecraft game events.
 */
public class Controller {
    private static Controller instance;
    private final Logger log;
    private final LinkedHashSet<LibShapeDraw> apiInstances;
    private int topApiInstanceId;
    private MinecraftAccess minecraftAccess;
    private boolean initialized;
    private long lastDump;

    private Controller() {
        if (GlobalSettings.isLoggingEnabled()) {
            log = new FileLogger(ModDirectory.DIRECTORY, ApiInfo.getName(), GlobalSettings.isLoggingAppend());
        } else {
            log = new NullLogger();
        }
        apiInstances = new LinkedHashSet<LibShapeDraw>();
        topApiInstanceId = 0;
        log.info(getClass().getName() + " instantiated");
    }

    public static Controller getInstance() {
        if (instance == null) {
            instance = new Controller();
        }
        return instance;
    }

    public static Logger getLog() {
        return getInstance().log;
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
     * Called by the bootstrapper.
     * Periodically dump API state to log if configured to do so.
     * Dispatch gameTick events.
     */
    public void gameTick(ReadonlyVector3 playerCoords) {
        log.finer("gameTick");
        if (GlobalSettings.getLoggingDebugDumpInterval() > 0) {
            long now = System.currentTimeMillis();
            if (now > lastDump + GlobalSettings.getLoggingDebugDumpInterval()) {
                dump();
                lastDump = now;
            }
        }
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
    }

    /**
     * Called by the bootstrapper.
     * Dispatch preRender events.
     * Render all registered shapes.
     */
    public void render(ReadonlyVector3 playerCoords, boolean isGuiHidden) {
        log.finer("render");

        int origDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        GL11.glPushMatrix();
        GL11.glTranslated(-playerCoords.getX(), -playerCoords.getY(), -playerCoords.getZ());

        for (LibShapeDraw apiInstance : apiInstances) {
            if (!apiInstance.getEventListeners().isEmpty()) {
                LSDPreRenderEvent event = new LSDPreRenderEvent(apiInstance, playerCoords, isGuiHidden);
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

        GL11.glPopMatrix();
        GL11.glDepthMask(true);
        GL11.glDepthFunc(origDepthFunc);
        GL11.glEnable(GL11.GL_LIGHTING);
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
}
