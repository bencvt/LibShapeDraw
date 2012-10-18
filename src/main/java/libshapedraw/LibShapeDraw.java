package libshapedraw;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import libshapedraw.event.LSDEventListener;
import libshapedraw.internal.LSDController;
import libshapedraw.internal.LSDInternalException;
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
        instanceId = LSDController.getInstance().registerApiInstance(this, ownerId);
    }

    /**
     * Permanently unregister from the internal Controller. Normally there's no
     * need to ever do this; it generally makes more sense to call
     * setVisible(false), remove event listeners, and/or clear the Shapes
     * collection if you want to temporarily shut things down.
     */
    public boolean unregister() {
        return LSDController.getInstance().unregisterApiInstance(this);
    }

    /** @deprecated use {@link ApiInfo#getVersion} */
    @Deprecated public static String getVersion() {
        return ApiInfo.getVersion();
    }

    /**
     * @return true if the backend controller is all set up.
     *     If false, the cause is likely one of:
     *     a) ModLoader or Forge is disabled/missing;
     *     b) mod_LibShapeDraw is disabled/missing; or
     *     c) ModLoader hasn't instantiated mod_LibShapeDraw yet
     *        because it's too early in the mod lifecycle.
     *        Wait until the load method or later.
     * 
     * @deprecated use {@link #verifyInitialized}
     */
    @Deprecated public static boolean isControllerInitialized() {
        return LSDController.isInitialized();
    }

    /**
     * Convenience method to make sure that LibShapeDraw is fully initialized,
     * with its rendering hooks properly installed. Throw an exception if not.
     * <p>
     * Assuming you're waiting to create the API instance until at least the
     * BaseMod.load method, it's a good idea to include this check, e.g.:
     * LibShapeDraw api = new LibShapeDraw().verifyInitialized();
     * <p>
     * This isn't included in the constructor because you're allowed to create
     * and populate API instances whenever you like, even if it's before
     * ModLoader/Forge has a chance to initialize mod_LibShapeDraw.
     */
    public LibShapeDraw verifyInitialized() {
        if (!LSDController.isInitialized()) {
            throw new LSDInternalException(ApiInfo.getName() +
                    " is not initialized. Possible causes:" +
                    " a) ModLoader or Forge is disabled/missing;" +
                    " b) mod_LibShapeDraw is disabled/missing; or" +
                    " c) another mod is incorrectly calling verifyInitialized too early.");
        }
        return this;
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
    /**
     * Convenience method, equivalent to getShapes().remove(shape).
     * This is not thread-safe.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw removeShape(Shape shape) {
        getShapes().remove(shape);
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
    /**
     * Convenience method, equivalent to getEventListeners().remove(listener)
     * This is not thread-safe.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw removeEventListener(LSDEventListener listener) {
        getEventListeners().remove(listener);
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
