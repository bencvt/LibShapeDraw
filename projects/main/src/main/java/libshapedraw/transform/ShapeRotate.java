package libshapedraw.transform;

import libshapedraw.animation.Animates;
import libshapedraw.animation.trident.Timeline;
import libshapedraw.primitive.Axis;
import libshapedraw.primitive.Vector3;

/**
 * Rotate a Shape by any number of degrees around any axis using glRotate.
 */
public class ShapeRotate implements ShapeTransform, Animates<Double> {
    private double angle;
    private Vector3 axis;
    private Timeline timelineAngle;

    public ShapeRotate(double angleDegrees, double axisX, double axisY, double axisZ) {
        this(angleDegrees, new Vector3(axisX, axisY, axisZ));
    }
    public ShapeRotate(double angleDegrees, Axis axis) {
        setAngle(angleDegrees);
        setAxis(axis);
    }
    public ShapeRotate(double angleDegrees, Vector3 axis) {
        setAngle(angleDegrees);
        setAxis(axis);
    }

    /**
     * The rotation angle, in degrees.
     */
    public double getAngle() {
        return angle;
    }
    /** @see #getAngle */
    public ShapeRotate setAngle(double angleDegrees) {
        angle = angleDegrees;
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
    /**
     * Convenience method, equivalent to
     * <code>setAxis(axis.unitVector.copy())</code>.
     */
    public ShapeRotate setAxis(Axis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("axis cannot be null");
        }
        return setAxis(axis.unitVector.copy());
    }

    @Override
    public void preRender() {
        axis.glApplyRotateDegrees(angle);
    }

    @Override
    public boolean isAnimating() {
        return timelineAngle != null && !timelineAngle.isDone();
    }

    @Override
    public ShapeRotate animateStop() {
        if (timelineAngle != null && !timelineAngle.isDone()) {
            timelineAngle.abort();
        }
        timelineAngle = null;
        return this;
    }

    @Override
    public ShapeRotate animateStart(Double toAngleDegrees, long durationMs) {
        if (toAngleDegrees == null) {
            throw new IllegalArgumentException("toAngleDegrees cannot be null");
        }
        newTimelineAngle(toAngleDegrees, durationMs);
        timelineAngle.play();
        return this;
    }

    @Override
    public ShapeRotate animateStartLoop(Double toAngleDegrees, boolean reverse, long durationMs) {
        if (toAngleDegrees == null) {
            throw new IllegalArgumentException("toAngleDegrees cannot be null");
        }
        newTimelineAngle(toAngleDegrees, durationMs);
        timelineAngle.playLoop(reverse);
        return this;
    }

    private void newTimelineAngle(double toAngleDegrees, long durationMs) {
        animateStop();
        timelineAngle = new Timeline(this);
        timelineAngle.addPropertyToInterpolate("angle", angle, toAngleDegrees);
        timelineAngle.setDuration(durationMs);
    }

    /** @deprecated use isAnimating */
    @Deprecated public boolean isAnimatingAngle() {
        return isAnimating();
    }
    /** @deprecated use animateStop */
    @Deprecated public ShapeRotate animateAngleStop() {
        return animateStop();
    }
    /** @deprecated use animateStart */
    @Deprecated public ShapeRotate animateAngleStart(double toAngleDegrees, long durationMs) {
        return animateStart(toAngleDegrees, durationMs);
    }
    /** @deprecated use animateStartLoop */
    @Deprecated public ShapeRotate animateAngleStartLoop(double toAngleDegrees, boolean reverse, long durationMs) {
        return animateAngleStartLoop(toAngleDegrees, reverse, durationMs);
    }
}
