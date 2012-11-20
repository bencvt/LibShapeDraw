package libshapedraw.transform;

import libshapedraw.animation.Animates;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

/**
 * Adjust the x/y/z coordinates of a Shape using glTranslate.
 */
public class ShapeTranslate implements ShapeTransform, Animates<ReadonlyVector3> {
    private Vector3 translateXYZ;

    public ShapeTranslate() {
        this(new Vector3(0.0, 0.0, 0.0));
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
        if (translateXYZ == null) {
            throw new IllegalArgumentException("translateXYZ cannot be null");
        }
        this.translateXYZ = translateXYZ;
        return this;
    }

    @Override
    public void preRender() {
        translateXYZ.glApplyTranslate();
    }

    @Override
    public boolean isAnimating() {
        return translateXYZ.isAnimating();
    }

    @Override
    public ShapeTranslate animateStop() {
        translateXYZ.animateStop();
        return this;
    }

    @Override
    public ShapeTranslate animateStart(ReadonlyVector3 toTranslate, long durationMs) {
        translateXYZ.animateStart(toTranslate, durationMs);
        return this;
    }
    /**
     * Convenience method, equivalent to:
     * <code>animateStart(new Vector3(toTranslateX, toTranslateY, toTranslateZ), durationMs)</code>
     */
    public ShapeTranslate animateStart(double toTranslateX, double toTranslateY, double toTranslateZ, long durationMs) {
        translateXYZ.animateStart(toTranslateX, toTranslateY, toTranslateZ, durationMs);
        return this;
    }

    @Override
    public ShapeTranslate animateStartLoop(ReadonlyVector3 toTranslate, boolean reverse, long durationMs) {
        translateXYZ.animateStartLoop(toTranslate, reverse, durationMs);
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStart(new Vector3(toTranslateX, toTranslateY, toTranslateZ), reverse, durationMs)</code>
     */
    public ShapeTranslate animateStartLoop(double toTranslateX, double toTranslateY, double toTranslateZ, boolean reverse, long durationMs) {
        translateXYZ.animateStartLoop(toTranslateX, toTranslateY, toTranslateZ, reverse, durationMs);
        return this;
    }
}
