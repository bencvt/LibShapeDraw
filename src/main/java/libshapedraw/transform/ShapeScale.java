package libshapedraw.transform;

import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class ShapeScale implements ShapeTransform {
    private Vector3 vector;
    public ShapeScale(double x, double y, double z) {
        this(new Vector3(x, y, z));
    }
    public ShapeScale(Vector3 vector) {
        setVector(vector);
    }
    public Vector3 getVector() {
        return vector;
    }
    public ShapeScale setVector(Vector3 vector) {
        this.vector = vector;
        return this;
    }
    @Override
    public void preRender() {
        GL11.glScaled(vector.getX(), vector.getY(), vector.getZ());
    }
}
