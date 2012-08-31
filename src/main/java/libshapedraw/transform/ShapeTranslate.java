package libshapedraw.transform;

import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class ShapeTranslate implements ShapeTransform {
    private Vector3 translateXYZ;
    public ShapeTranslate() {
        this(0.0, 0.0, 0.0);
    }
    public ShapeTranslate(double translateX, double translateY, double translateZ) {
        this(new Vector3(translateX, translateY, translateZ));
    }
    public ShapeTranslate(Vector3 vector) {
        setTranslateXYZ(vector);
    }
    public Vector3 getTranslateXYZ() {
        return translateXYZ;
    }
    public ShapeTranslate setTranslateXYZ(Vector3 translateXYZ) {
        this.translateXYZ = translateXYZ;
        return this;
    }
    @Override
    public void preRender() {
        GL11.glTranslated(translateXYZ.getX(), translateXYZ.getY(), translateXYZ.getZ());
    }
}
