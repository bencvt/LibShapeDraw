package libshapedraw.shape;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import libshapedraw.transform.ShapeTransform;

import org.lwjgl.opengl.GL11;

public abstract class Shape {
    private boolean visible = true;
    private Vector3 origin;
    private boolean relativeToOrigin = true;
    private List<ShapeTransform> transforms;

    public Shape(Vector3 origin) {
        setOrigin(origin);
    }

    /**
     * If false, the Shape will not be rendered.
     */
    public boolean isVisible() {
        return visible;
    }
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
    protected Vector3 getOrigin() {
        return origin;
    }
    protected void setOrigin(Vector3 origin) {
        if (origin == null) {
            throw new NullPointerException();
        }
        this.origin = origin;
    }

    /**
     * If true, render this shape relative to its own origin x/y/z.
     * If false, rendering will ignore the shape's origin, operating on
     * absolute world x/y/z coordinates.
     */
    public boolean isRelativeToOrigin() {
        return relativeToOrigin;
    }
    protected void setRelativeToOrigin(boolean relativeToOrigin) {
        this.relativeToOrigin = relativeToOrigin;
    }

    /**
     * Get the list of ShapeTransforms to perform right before rendering this
     * Shape, if any.
     * This is not thread-safe.
     */
    public List<ShapeTransform> getTransforms() {
        if (transforms == null) {
            // lazily create the list; many shapes don't need transforms
            transforms = Collections.checkedList(new LinkedList<ShapeTransform>(), ShapeTransform.class);
        }
        return transforms;
    }
    /**
     * Convenience method, equivalent to getTransforms().add(transform)
     * This is not thread-safe.
     * @returns the instance (for method chaining)
     */
    public Shape addTransform(ShapeTransform transform) {
        getTransforms().add(transform);
        return this;
    }

    /**
     * Render the Shape, if visible. Also perform any ShapeTransforms
     * registered to this Shape. This should normally only be called internally
     * by the Controller.
     */
    public final void render(MinecraftAccess mc) {
        if (!isVisible()) {
            return;
        }
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
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
    }

    /**
     * Render the Shape, called by the Shape's main render method.
     */
    protected abstract void renderShape(MinecraftAccess mc);

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append(getClass().getSimpleName()).append('@').append(Integer.toHexString(hashCode())).append('{');
        if (isVisible()) {
            b.append('V');
        }
        if (transforms != null) {
            for (int i = 0; i < transforms.size(); i++) {
                b.append('T');
            }
        }
        b.append('}');
        return b.toString();
    }
}
