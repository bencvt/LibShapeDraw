package libshapedraw.transform;

import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class ShapeTranslate implements ShapeTransform {
    private Vector3 vector;
    public ShapeTranslate(double x, double y, double z) {
        this(new Vector3(x, y, z));
    }
    public ShapeTranslate(Vector3 vector) {
        setVector(vector);
    }
    public Vector3 getVector() {
        return vector;
    }
    public ShapeTranslate setVector(Vector3 vector) {
        this.vector = vector;
        return this;
    }
    public void preRender() {
        GL11.glTranslated(vector.getX(), vector.getY(), vector.getZ());
    }
}
