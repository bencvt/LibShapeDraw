package libshapedraw.transform;

import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class ShapeRotate implements ShapeTransform {
    private float angle;
    private Vector3 axis;
    public ShapeRotate(float angle, double x, double y, double z) {
        this(angle, new Vector3(x, y, z));
    }
    public ShapeRotate(float angle, Vector3 axis) {
        setAngle(angle);
        setAxis(axis);
    }
    public double getAngle() {
        return angle;
    }
    public ShapeRotate setAngle(float angle) {
        this.angle = angle;
        return this;
    }
    public Vector3 getAxis() {
        return axis;
    }
    public ShapeRotate setAxis(Vector3 axis) {
        this.axis = axis;
        return this;
    }
    @Override
    public void preRender() {
        // GL11.glRotated not available in Minecraft's LWJGL version
        GL11.glRotatef(angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
    }
}
