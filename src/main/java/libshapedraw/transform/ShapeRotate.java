package libshapedraw.transform;

import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * Rotate a Shape by any number of degrees around any axis using glRotate.
 */
public class ShapeRotate implements ShapeTransform {
    private double angle;
    private Vector3 axis;
    public ShapeRotate(double angle, double axisX, double axisY, double axisZ) {
        this(angle, new Vector3(axisX, axisY, axisZ));
    }
    public ShapeRotate(double angle, Vector3 axis) {
        setAngle(angle);
        setAxis(axis);
    }
    /**
     * The rotation angle, in degrees.
     */
    public double getAngle() {
        return angle;
    }
    /** @see #getAngle */
    public ShapeRotate setAngle(double angle) {
        this.angle = angle;
        return this;
    }
    /**
     * The axis to rotate around, e.g. (0.0, 1.0, 0.0) rotates around the y-axis.
     */
    public Vector3 getAxis() {
        return axis;
    }
    /** @see #getAxis */
    public ShapeRotate setAxis(Vector3 axis) {
        this.axis = axis;
        return this;
    }

    @Override
    public void preRender() {
        // GL11.glRotated not available in Minecraft's LWJGL version
        GL11.glRotatef((float) angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
    }
}
