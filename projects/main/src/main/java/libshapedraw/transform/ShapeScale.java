package libshapedraw.transform;

import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * Resize a Shape using glScale.
 */
public class ShapeScale implements ShapeTransform {
    private Vector3 scaleXYZ;
    public ShapeScale() {
        this(1.0, 1.0, 1.0);
    }
    public ShapeScale(double scaleX, double scaleY, double scaleZ) {
        this(new Vector3(scaleX, scaleY, scaleZ));
    }
    public ShapeScale(Vector3 scaleXYZ) {
        setScaleXYZ(scaleXYZ);
    }
    public Vector3 getScaleXYZ() {
        return scaleXYZ;
    }
    public ShapeScale setScaleXYZ(Vector3 scaleXYZ) {
        this.scaleXYZ = scaleXYZ;
        return this;
    }
    @Override
    public void preRender() {
        GL11.glScaled(scaleXYZ.getX(), scaleXYZ.getY(), scaleXYZ.getZ());
    }
}
