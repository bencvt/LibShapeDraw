package libshapedraw;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import libshapedraw.event.LSDEventListener;
import libshapedraw.internal.Controller;
import libshapedraw.shape.Shape;

/**
 * Main API entry point, instantiated by client code.
 * Multiple API instances can coexist, each with their own settings and state.
 * <p>
 * Instantiating this class will automatically register it with the internal
 * Controller. Any shapes you add with be rendered and any listeners you add
 * will receive events.
 * <p>
 * API instances are not thread-safe. Accessing or modifying getShapes() (or
 * any other exposed collection) from any thread other than the main Minecraft
 * game thread may result in non-deterministic behavior.
 * <p>
 * @see the demos in src/test/java for sample usage
 */
public class LibShapeDraw {
    private final Set<Shape> shapes;
    private final Set<LSDEventListener> eventListeners;
    private final String instanceId;
    private boolean visible = true;
    private boolean visibleWhenHidingGui = false;

    public LibShapeDraw() {
        this(Thread.currentThread().getStackTrace()[2].toString());
    }
    public LibShapeDraw(String ownerId) {
        shapes = Collections.checkedSet(new LinkedHashSet<Shape>(), Shape.class);
        eventListeners = Collections.checkedSet(new LinkedHashSet<LSDEventListener>(), LSDEventListener.class);
        instanceId = Controller.getInstance().registerApiInstance(this, ownerId);
    }

    public boolean unregister() {
        return Controller.getInstance().unregisterApiInstance(this);
    }

    /** @see ApiInfo */
    public static String getVersion() {
        return ApiInfo.getVersion();
    }

    /**
     * @return true if the backend controller is all set up.
     *     If false, the cause is likely one of:
     *     a) ModLoader is disabled/missing;
     *     b) mod_LibShapeDraw is disabled/missing; or
     *     c) ModLoader hasn't instantiated mod_LibShapeDraw yet
     *        because it's too early in the mod lifecycle.
     *        Wait until the load method or later.
     */
    public static boolean isControllerInitialized() {
        return Controller.isInitialized();
    }

    public boolean isVisible() {
        return visible;
    }
    public LibShapeDraw setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public boolean isVisibleWhenHidingGui() {
        return visibleWhenHidingGui;
    }
    public LibShapeDraw setVisibleWhenHidingGui(boolean visibleWhenHidingGui) {
        this.visibleWhenHidingGui = visibleWhenHidingGui;
        return this;
    }

    /** This is not thread-safe. */
    public Set<Shape> getShapes() {
        return shapes;
    }
    /**
     * Convenience method, equivalent to getShapes().add(shape).
     * This is not thread-safe.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw addShape(Shape shape) {
        getShapes().add(shape);
        return this;
    }

    /** This is not thread-safe. */
    public Set<LSDEventListener> getEventListeners() {
        return eventListeners;
    }
    /**
     * Convenience method, equivalent to getEventListeners().add(listener)
     * This is not thread-safe.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw addEventListener(LSDEventListener listener) {
        getEventListeners().add(listener);
        return this;
    }

    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return getInstanceId();
    }
}
