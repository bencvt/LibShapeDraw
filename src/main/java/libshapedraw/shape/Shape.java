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

    public boolean isVisible() {
        return visible;
    }
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    public List<ShapeTransform> getTransforms() {
        if (transforms == null) {
            transforms = Collections.checkedList(new LinkedList<ShapeTransform>(), ShapeTransform.class);
        }
        return transforms;
    }
    /**
     * Convenience method, equivalent to getTransforms().add(transform)
     * @returns the instance (for method chaining)
     */
    public Shape addTransform(ShapeTransform transform) {
        getTransforms().add(transform);
        return this;
    }

    public final void render(MinecraftAccess mc, ReadonlyVector3 coords) {
        if (!isVisible()) {
            return;
        }
        if (transforms == null || transforms.isEmpty()) {
            renderShape(mc);
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslated(coords.getX(), coords.getY(), coords.getZ());
        for (ShapeTransform t : transforms) {
            if (t != null) {
                t.preRender();
            }
        }
        GL11.glTranslated(-coords.getX(), -coords.getY(), -coords.getZ());
        renderShape(mc);
        GL11.glPopMatrix();
    }

    public abstract void renderShape(MinecraftAccess mc);

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
