package libshapedraw;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import libshapedraw.event.LSDEventListener;
import libshapedraw.internal.LSDController;
import libshapedraw.internal.LSDInternalException;
import libshapedraw.shape.Shape;

/**
 * Main entry point for the LibShapeDraw API.
 * <p>
 * Instantiating this class will automatically register it. Shapes added using
 * addShape will be rendered. LSDEventListeners added using addEventListener
 * will receive events.
 * <p>
 * Multiple API instances can coexist, each with their own settings and state.
 * <p>
 * See the demos in projects/demos/src/main/java for sample usage.
 */
public class LibShapeDraw {
    private final Set<Shape> shapes;
    private final Set<Shape> shapesReadonly;
    private final Set<LSDEventListener> eventListeners;
    private final Set<LSDEventListener> eventListenersReadonly;
    private final String instanceId;
    private boolean visible = true;
    private boolean visibleWhenHidingGui = false;

    public LibShapeDraw() {
        this(Thread.currentThread().getStackTrace()[2].toString());
    }

    /**
     * @param ownerId optional identifier for the mod that is using this API
     *                instance.
     */
    public LibShapeDraw(String ownerId) {
        shapes = Collections.checkedSet(new LinkedHashSet<Shape>(), Shape.class);
        shapesReadonly = Collections.unmodifiableSet(shapes);
        eventListeners = Collections.checkedSet(new LinkedHashSet<LSDEventListener>(), LSDEventListener.class);
        eventListenersReadonly = Collections.unmodifiableSet(eventListeners);
        instanceId = LSDController.getInstance().registerApiInstance(this, ownerId);
    }

    /**
     * Permanently unregister the entire API instance. It will no longer render
     * its Shapes or dispatch LSDEvents to registered listeners.
     * <p>
     * Normally there's no need to ever do this. It generally makes more sense
     * to call setVisible(false) or clearShapes() to temporarily shut things
     * down.
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

    /** @see #setVisible */
    public boolean isVisible() {
        return visible;
    }

    /**
     * If set to false, none of the shapes owned by this API instance will be
     * rendered. Defaults to true.
     * <p>
     * When set to false, the shapes still exist. They just won't be rendered
     * until this property is set to true again.
     * <p>
     * To show/hide individual shapes rather than the entire shape collection,
     * use {@link Shape#setVisible}.
     */
    public LibShapeDraw setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /** @see #setVisibleWhenHidingGui */
    public boolean isVisibleWhenHidingGui() {
        return visibleWhenHidingGui;
    }

    /**
     * If set to true, the shapes owned by this API instance are rendered
     * regardless of whether the GUI is visible. In other words, the shapes are
     * considered part of the game world rather than part of the GUI.
     * <p>
     * If set to false (default), the shapes are considered part of the GUI.
     * They will be appropriately hidden when the user presses F1 to hide the
     * GUI.
     * <p>
     * @see {@link #setVisible}, which overrides this property when set.
     */
    public LibShapeDraw setVisibleWhenHidingGui(boolean visibleWhenHidingGui) {
        this.visibleWhenHidingGui = visibleWhenHidingGui;
        return this;
    }

    /**
     * Get a read-only view of the full set of shapes registered to this API
     * instance. To modify this set use addShape, removeShape, and clearShapes.
     */
    public Set<Shape> getShapes() {
        return shapesReadonly;
    }

    /**
     * Register a Shape to be rendered by this API instance.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw addShape(Shape shape) {
        if (shape == null) {
            throw new IllegalArgumentException("shape cannot be null");
        }
        if (shapes.add(shape)) {
            shape.onAdd(this);
        }
        return this;
    }

    /**
     * Unregister a Shape, no longer rendering it.
     * <p>
     * Attempting to remove a shape that is not part of this API instance's
     * shape collection is allowed but won't do anything.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * 
     * @see {@link Shape#setVisible} for an alternate way of preventing a shape
     *      from rendering. Generally, removeShape should be called if the
     *      shape will never be rendered again. If the shape just needs to be
     *      hidden temporarily, use setVisible.
     * 
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw removeShape(Shape shape) {
        if (shapes.remove(shape)) {
            shape.onRemove(this);
        }
        return this;
    }

    /**
     * Unregister all Shapes owned by this API instance, no longer rendering
     * them.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * 
     * @see {@link setVisible} for an alternate way of preventing all shapes
     *      from rendering. Generally, clearShapes should be called if the
     *      current shapes will never be rendered again. If the shapes just
     *      need to be hidden temporarily, use setVisible.
     * 
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw clearShapes() {
        LinkedHashSet<Shape> prev = new LinkedHashSet<Shape>(shapes);
        shapes.clear();
        for (Shape shape : prev) {
            shape.onRemove(this);
        }
        return this;
    }

    /**
     * Get a read-only view of the set of event listeners associated with this
     * API instance. To modify this set use addEventListener,
     * removeEventListener, and clearEventListeners.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     */
    public Set<LSDEventListener> getEventListeners() {
        return eventListenersReadonly;
    }

    /**
     * Register an event listener to receive LSDEvents.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw addEventListener(LSDEventListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener cannot be null");
        }
        eventListeners.add(listener);
        return this;
    }

    /**
     * Unregister an event listener. It will no longer receive LSDEvents.
     * <p>
     * Normally there is no need to manually unregister event listeners, but
     * this method is here if needed.
     * <p>
     * Attempting to remove an event listener that is not registered to this
     * API instance is allowed but won't do anything.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw removeEventListener(LSDEventListener listener) {
        eventListeners.remove(listener);
        return this;
    }

    /**
     * Unregister all event listeners that were registered to this API
     * instance.
     * <p>
     * Normally there is no need to manually unregister event listeners, but
     * this method is here if needed.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * @returns the instance (for method chaining)
     */
    public LibShapeDraw clearEventListeners() {
        eventListeners.clear();
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
