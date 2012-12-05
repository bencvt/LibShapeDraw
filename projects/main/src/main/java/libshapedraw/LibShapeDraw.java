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
 * Instantiating this class will automatically register it with the internal
 * controller. Shapes added using addShape will be rendered. LSDEventListeners
 * added using addEventListener will receive events.
 * <p>
 * Multiple API instances can coexist, each with their own settings and state.
 * <p>
 * See the demos in
 * <a href="https://github.com/bencvt/LibShapeDraw/tree/master/projects/demos/src/main/java/libshapedraw/demos">projects/demos</a>
 * for sample usage.
 */
public class LibShapeDraw {
    private final Set<Shape> shapes;
    private final Set<Shape> shapesReadonly;
    private final Set<LSDEventListener> eventListeners;
    private final Set<LSDEventListener> eventListenersReadonly;
    private final String instanceId;
    private final String ownerId;
    private boolean visible = true;
    private boolean visibleWhenHidingGui = false;

    /**
     * Create a new API instance, automatically registering it with the
     * internal controller.
     */
    public LibShapeDraw() {
        this(Thread.currentThread().getStackTrace()[2].toString());
    }

    /**
     * The ownerId is optional: an arbitrary identifier for the mod that owns
     * this API instance.
     */
    public LibShapeDraw(String ownerId) {
        if (ownerId == null) {
            ownerId = Thread.currentThread().getStackTrace()[2].toString();
        }
        shapes = new LinkedHashSet<Shape>();
        shapesReadonly = Collections.unmodifiableSet(shapes);
        eventListeners = new LinkedHashSet<LSDEventListener>();
        eventListenersReadonly = Collections.unmodifiableSet(eventListeners);
        instanceId = LSDController.getInstance().registerApiInstance(this, ownerId);
        this.ownerId = ownerId;
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

    /** @deprecated use {@link ApiInfo} */
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
     * Optional sanity check for mods to make sure that ModLoader or Forge has
     * successfully initialized LibShapeDraw. Throws an exception if not.
     * <p>
     * This is the only LibShapeDraw API method that has a restriction on when
     * you can call it. Always wait until at least the BaseMod.load method.
     * <p>
     * In other words, do not call verifyInitialized() from your mod's
     * constructor. If you do, the exception may be thrown depending on the
     * order that ModLoader or Forge chooses to instantiate the various mod
     * classes.
     */
    public LibShapeDraw verifyInitialized() {
        // This method can't be automatically called from the constructor
        // because it is valid for client code to create LibShapeDraw instances
        // at any point... even before ModLoader/Forge has had a chance to
        // instantiated mod_LibShapeDraw.
        if (!LSDController.isInitialized()) {
            throw new LSDInternalException(ApiInfo.getName() +
                    " is not initialized. Possible causes:" +
                    " a) ModLoader or Forge is disabled/missing;" +
                    " b) mod_LibShapeDraw is disabled/missing; or" +
                    " c) another mod is incorrectly calling verifyInitialized too early.");
        }
        return this;
    }

    /**
     * Get a {@link MinecraftAccess} instance for calling obfuscated
     * Minecraft methods.
     */
    public MinecraftAccess getMinecraftAccess() {
        return LSDController.getMinecraftAccess();
    }

    /**
     * Whether this API instance's shapes will be rendered. Defaults to true.
     * When set to false, the shapes still exist. They just won't be rendered
     * until this property is set to true again.
     * <p>
     * To show/hide individual shapes rather than the entire shape collection,
     * use {@link Shape#setVisible}.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Whether this API instance's shapes will be rendered. Defaults to true.
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

    /**
     * Whether this API instance's shapes are considered part of the game world
     * (true) or part of the GUI (false, default).
     * 
     * @see #setVisibleWhenHidingGui
     */
    public boolean isVisibleWhenHidingGui() {
        return visibleWhenHidingGui;
    }

    /**
     * Whether this API instance's shapes are considered part of the game world
     * (true) or part of the GUI (false, default).
     * <p>
     * If set to true, the shapes owned by this API instance are rendered
     * regardless of whether the GUI is visible. In other words, the shapes are
     * considered part of the game world rather than part of the GUI.
     * <p>
     * If set to false (default), the shapes are considered part of the GUI.
     * They will be appropriately hidden when the user presses F1 to hide the
     * GUI.
     * <p>
     * See also {@link #setVisible}, which overrides this property when set.
     */
    public LibShapeDraw setVisibleWhenHidingGui(boolean visibleWhenHidingGui) {
        this.visibleWhenHidingGui = visibleWhenHidingGui;
        return this;
    }

    /**
     * Get a read-only view of the set of shapes registered to this API
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
     * @return the instance (for method chaining)
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
     * If the Shape has Colors or ShapeTransforms that are busy animating, they
     * will continue to do so even after the Shape is unregistered. The
     * animations can be manually stopped using animateStop, or simply allowed
     * to expire normally (if non-looping).
     * <p>
     * Animations are not automatically stopped here because it is perfectly
     * valid (and often desired, for synchronized animation) for multiple
     * Shapes to share Color/ShapeTransform instances.
     * <p>
     * Attempting to remove a shape that is not part of this API instance's
     * shape collection is allowed but won't do anything.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * <p>
     * See also {@link Shape#setVisible} for an alternate way of preventing a
     * shape from rendering. Generally, removeShape should be called if the
     * shape will never be rendered again. If the shape just needs to be hidden
     * temporarily, use setVisible.
     * 
     * @return the instance (for method chaining)
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
     * Any animations affecting the Shapes are <b>not</b> automatically
     * stopped. See {@link #removeShape}'s documentation for more details.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * <p>
     * See also {@link #setVisible} for an alternate way of preventing all
     * shapes from rendering. Generally, clearShapes should be called if the
     * current shapes will never be rendered again. If the shapes just need to
     * be hidden temporarily, use setVisible.
     * 
     * @return the instance (for method chaining)
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
     * @return the instance (for method chaining)
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
     * @return the instance (for method chaining)
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
     * @return the instance (for method chaining)
     */
    public LibShapeDraw clearEventListeners() {
        eventListeners.clear();
        return this;
    }

    /**
     * Output the complete state of all LibShapeDraw API instances, including
     * this one, to <code>(minecraft-dir)/mods/LibShapeDraw/LibShapeDraw.log</code>.
     * @return false if logging was disabled by user preference. Logging is
     *         enabled by default, however.
     */
    public boolean debugDump() {
        return LSDController.getInstance().debugDump();
    }

    /**
     * Get the unique id assigned to this API instance by the internal
     * controller. E.g.: "LibShapeDraw#0".
     */
    public String getInstanceId() {
        return instanceId;
    }

    @Override
    public String toString() {
        return getInstanceId() + ":" + ownerId;
    }
}
