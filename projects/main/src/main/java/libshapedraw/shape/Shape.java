package libshapedraw.shape;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import libshapedraw.LibShapeDraw;
import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import libshapedraw.transform.ShapeTransform;

import org.lwjgl.opengl.GL11;

/**
 * Generic base class for a renderable object.
 */
public abstract class Shape {
    private boolean visible = true;
    private Vector3 origin;
    private boolean relativeToOrigin = true;
    private List<ShapeTransform> transforms;
    private List<ShapeTransform> transformsReadonly;

    public Shape(Vector3 origin) {
        setOrigin(origin);
    }

    /** If false, the Shape will not be rendered. */
    public boolean isVisible() {
        return visible;
    }
    /** If false, the Shape will not be rendered. */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * The point around which ShapeTransforms should occur. This is generally
     * the center point of the Shape, if that makes sense for the Shape type.
     */
    public ReadonlyVector3 getOriginReadonly() {
        return origin;
    }
    /** @see #getOriginReadonly */
    protected Vector3 getOrigin() {
        return origin;
    }
    /** @see #getOriginReadonly */
    protected void setOrigin(Vector3 origin) {
        if (origin == null) {
            throw new IllegalArgumentException("origin cannot be null");
        }
        this.origin = origin;
    }

    /**
     * If true, render this shape relative to its own origin x/y/z.
     * If false, rendering will ignore the shape's origin, operating
     * on absolute world x/y/z coordinates.
     * <p>
     * Which setting is the default, and whether it's possible to change this
     * setting, depends on the Shape type.
     */
    public boolean isRelativeToOrigin() {
        return relativeToOrigin;
    }
    /** @see #isRelativeToOrigin */
    protected void setRelativeToOrigin(boolean relativeToOrigin) {
        this.relativeToOrigin = relativeToOrigin;
    }

    /** Lazily create the lists. Many shapes don't need transforms. */
    private void makeTransforms() {
        if (transforms == null) {
            transforms = new LinkedList<ShapeTransform>();
            transformsReadonly = Collections.unmodifiableList(transforms);
        }
    }

    /**
     * Get a read-only view of the list of ShapeTransforms to perform right
     * before rendering this Shape, if any. To modify this list use
     * addTransform, removeTransform, and clearTransforms.
     */
    public List<ShapeTransform> getTransforms() {
        makeTransforms();
        return transformsReadonly;
    }

    /**
     * Register a ShapeTransform to be applied to this Shape.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * 
     * @return the instance (for method chaining)
     */
    public Shape addTransform(ShapeTransform transform) {
        makeTransforms();
        if (transform == null) {
            throw new IllegalArgumentException("transform cannot be null");
        }
        transforms.add(transform);
        return this;
    }

    /**
     * Unregister a ShapeTransform, no longer applying it to this Shape.
     * <p>
     * Attempting to remove a transform that is not registered to this shape
     * is allowed but won't do anything.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * 
     * @return the instance (for method chaining)
     */
    public Shape removeTransform(ShapeTransform transform) {
        makeTransforms();
        transforms.remove(transform);
        return this;
    }

    /**
     * Unregister all ShapeTransforms registered to this Shape.
     * <p>
     * Thread safety is not guaranteed. To avoid non-deterministic behavior,
     * only call this method from the main Minecraft thread.
     * 
     * @return the instance (for method chaining)
     */
    public Shape clearTransforms() {
        makeTransforms();
        transforms.clear();
        return this;
    }

    /**
     * Called whenever this Shape is added to a LibShapeDraw API instance's set
     * of shapes to render.
     */
    public void onAdd(LibShapeDraw apiInstance) {
        // do nothing; derived classes can override as needed.
    }

    /**
     * Called whenever this Shape is removed from a LibShapeDraw API instance's
     * set of shapes to render.
     * <p>
     * This method should clean up any external resources (such as VBOs) that
     * were owned by this Shape.
     */
    public void onRemove(LibShapeDraw apiInstance) {
        // do nothing; derived classes can override as needed.
    }

    /**
     * Called immediately before this Shape is rendered.
     * <p>
     * Calls glContextStandardSetup.
     */
    public void onPreRender(MinecraftAccess mc) {
        glContextStandardSetup();
    }

    /**
     * Resets common OpenGL context settings, helping ensure that Shapes will
     * render the same regardless of what was rendered beforehand.
     */
    public static final void glContextStandardSetup() {
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHT1);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glLineWidth(1.0F);
        // Every Shape calls glColord; no need to reset that here.
    }

    /**
     * Called immediately after this Shape is rendered.
     * <p>
     * If any unique OpenGL context settings were applied but not reset during
     * rendering, they should be reset here. Otherwise, later elements may be
     * rendered incorrectly.
     */
    public void onPostRender(MinecraftAccess mc) {
        // do nothing; derived classes can override as needed.
    }

    /**
     * Render the Shape, if visible. Also perform any ShapeTransforms
     * registered to this Shape.
     * <p>
     * This method is automatically called as appropriate when a Shape has been
     * registered to a LibShapeDraw API instance using addShape.
     * <p>
     * It can also be called manually for Shapes not associated with an API
     * instance.
     */
    public final void render(MinecraftAccess mc) {
        if (!isVisible()) {
            return;
        }
        onPreRender(mc);
        final boolean absolute = !isRelativeToOrigin();
        if (absolute && transforms == null) {
            renderShape(mc);
        } else {
            final ReadonlyVector3 origin = getOriginReadonly();
            if (origin == null) {
                return;
            }
            GL11.glPushMatrix();
            GL11.glTranslated(origin.getX(), origin.getY(), origin.getZ());
            if (transforms != null) {
                for (ShapeTransform t : transforms) {
                    if (t != null) {
                        t.preRender();
                    }
                }
            }
            if (absolute) {
                GL11.glTranslated(-origin.getX(), -origin.getY(), -origin.getZ());
            }
            renderShape(mc);
            GL11.glPopMatrix();
        }
        onPostRender(mc);
    }

    /**
     * Does the actual work of rendering the Shape, called by the Shape's main
     * render method after setting up the OpenGL context.
     */
    protected abstract void renderShape(MinecraftAccess mc);

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder()
        .append(getClass().getSimpleName())
        .append('@')
        .append(Integer.toHexString(hashCode()))
        .append('{');
        if (isRelativeToOrigin()) {
            b.append('R');
        }
        if (isVisible()) {
            b.append('V');
        }
        if (transforms != null) {
            for (int i = 0; i < transforms.size(); i++) {
                b.append('T');
            }
        }
        return b.append('}').append(getOriginReadonly()).toString();
    }
}
