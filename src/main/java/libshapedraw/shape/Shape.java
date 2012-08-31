package libshapedraw.shape;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.transform.ShapeTransform;

import org.lwjgl.opengl.GL11;

public abstract class Shape {
    private boolean visible = true;
    private List<ShapeTransform> transforms;

    /**
     * The point around which ShapeTransforms should occur. Must not be null.
     * This should generally be the center point of the Shape, if that makes
     * sense for the Shape type.
     */
    public abstract ReadonlyVector3 getOrigin();

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
        if (transforms == null || transforms.isEmpty()) {
            renderShape(mc);
            return;
        }
        ReadonlyVector3 origin = getOrigin();
        GL11.glPushMatrix();
        GL11.glTranslated(origin.getX(), origin.getY(), origin.getZ());
        for (ShapeTransform t : transforms) {
            if (t != null) {
                t.preRender();
            }
        }
        GL11.glTranslated(-origin.getX(), -origin.getY(), -origin.getZ());
        renderShape(mc);
        GL11.glPopMatrix();
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
