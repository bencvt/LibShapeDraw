package libshapedraw.transform;

import libshapedraw.animation.trident.Timeline;
import libshapedraw.primitive.Axis;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * Rotate a Shape by any number of degrees around any axis using glRotate.
 */
public class ShapeRotate implements ShapeTransform {
    private double angle;
    private Vector3 axis;
    private Timeline timelineAngle;

    public ShapeRotate(double angle, double axisX, double axisY, double axisZ) {
        this(angle, new Vector3(axisX, axisY, axisZ));
    }
    public ShapeRotate(double angle, Axis axis) {
        this(angle, Vector3.ZEROS.copy().setComponent(axis, 1.0));
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
        if (axis == null) {
            throw new IllegalArgumentException("axis cannot be null");
        }
        this.axis = axis;
        return this;
    }

    @Override
    public void preRender() {
        // GL11.glRotated not available in Minecraft's LWJGL version
        GL11.glRotatef((float) angle, (float) axis.getX(), (float) axis.getY(), (float) axis.getZ());
    }

    /**
     * @return true if the angle is being updated by an active animation.
     */
    public boolean isAnimatingAngle() {
        return timelineAngle != null && !timelineAngle.isDone();
    }

    /**
     * If there is an active animation changing the angle, stop it abruptly,
     * leaving the angle in an intermediate state.
     * <p>
     * After calling this method it is safe to modify the angle using setAngle
     * without being overwritten by the animation.
     * 
     * @return the same ShapeRotate object, modified in-place.
     */
    public ShapeRotate animateAngleStop() {
        if (timelineAngle != null && !timelineAngle.isDone()) {
            timelineAngle.abort();
            timelineAngle = null;
        }
        return this;
    }

    /**
     * Animate the ShapeRotate's angle, spinning the target Shape(s).
     * <p>
     * After starting an animation, modifying the angle using setAngle is a bad
     * idea, as the animation will be frequently overwriting it. Either wait
     * for the animation to complete or use {@link #animateAngleStop} to halt
     * the animation early.
     * <p>
     * This is a convenience method; for more control over the animation,
     * an external Timeline can be used. Just don't have two Timelines trying
     * to update the same property.
     * 
     * @param toAngle the angle in degrees to animate to
     * @param durationMs interval in milliseconds
     * @return the same ShapeRotate object, modified in-place.
     */
    public ShapeRotate animateAngleStart(double toAngle, long durationMs) {
        newTimelineAngle(toAngle, durationMs);
        timelineAngle.play();
        return this;
    }

    /**
     * Animate the ShapeRotate's angle, spinning the target Shape(s).
     * The animation loops indefinitely, going back and forth between the
     * original angle and the specified angle.
     * <p>
     * After starting an animation, modifying the angle using setAngle is a bad
     * idea, as the animation will be frequently overwriting it. Use
     * {@link #animateStop} to halt the animation.
     * <p>
     * This is a convenience method; for more control over the animation,
     * an external Timeline can be used. Just don't have two Timelines trying
     * to update the same properties.
     * 
     * @param toScale the angle in degrees to animate to
     * @param reverse if true, rotate back to the original angle each time
     *        the animation loops. If false, jump back to the original angle
     *        immediately each time.
     *        <p>
     *        A common use case is to just spin the shape 360 degrees in one
     *        direction. To do this, simply call:
     *        animateAngleStartLoop(getAngle() + 360.0, false, durationMs)
     * @param durationMs interval in milliseconds
     * @return the same ShapeRotate object, modified in-place.
     */
    public ShapeRotate animateAngleStartLoop(double toAngle, boolean reverse, long durationMs) {
        newTimelineAngle(toAngle, durationMs);
        timelineAngle.playLoop(reverse);
        return this;
    }

    private void newTimelineAngle(double toAngle, long durationMs) {
        animateAngleStop();
        timelineAngle = new Timeline(this);
        timelineAngle.addPropertyToInterpolate("angle", angle, toAngle);
        timelineAngle.setDuration(durationMs);
    }
}
