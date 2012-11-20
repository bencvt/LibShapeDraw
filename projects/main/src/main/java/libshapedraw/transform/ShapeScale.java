package libshapedraw.transform;

import libshapedraw.animation.Animates;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

/**
 * Resize a Shape using glScale.
 */
public class ShapeScale implements ShapeTransform, Animates<ReadonlyVector3> {
    private Vector3 scaleXYZ;

    public ShapeScale() {
        this(new Vector3(1.0, 1.0, 1.0));
    }
    public ShapeScale(double scaleX, double scaleY, double scaleZ) {
        this(new Vector3(scaleX, scaleY, scaleZ));
    }
    public ShapeScale(double scale) {
        this(new Vector3(scale, scale, scale));
    }
    public ShapeScale(Vector3 scaleXYZ) {
        setScaleXYZ(scaleXYZ);
    }

    public Vector3 getScaleXYZ() {
        return scaleXYZ;
    }
    public ShapeScale setScaleXYZ(Vector3 scaleXYZ) {
        if (scaleXYZ == null) {
            throw new IllegalArgumentException("scaleXYZ cannot be null");
        }
        this.scaleXYZ = scaleXYZ;
        return this;
    }

    @Override
    public void preRender() {
        scaleXYZ.glApplyScale();
    }

    @Override
    public boolean isAnimating() {
        return scaleXYZ.isAnimating();
    }

    @Override
    public ShapeScale animateStop() {
        scaleXYZ.animateStop();
        return this;
    }

    @Override
    public ShapeScale animateStart(ReadonlyVector3 toScale, long durationMs) {
        scaleXYZ.animateStart(toScale, durationMs);
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStart(new Vector3(toScaleX, toScaleY, toScaleZ), durationMs);</code>
     */
    public ShapeScale animateStart(double toScaleX, double toScaleY, double toScaleZ, long durationMs) {
        scaleXYZ.animateStart(toScaleX, toScaleY, toScaleZ, durationMs);
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStart(new Vector3(toScale, toScale, toScale), durationMs);</code>
     */
    public ShapeScale animateStart(double toScale, long durationMs) {
        scaleXYZ.animateStart(toScale, toScale, toScale, durationMs);
        return this;
    }

    @Override
    public ShapeScale animateStartLoop(ReadonlyVector3 toScale, boolean reverse, long durationMs) {
        scaleXYZ.animateStartLoop(toScale, reverse, durationMs);
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStartLoop(new Vector3(toScaleX, toScaleY, toScaleZ), reverse, durationMs);</code>
     */
    public ShapeScale animateStartLoop(double toScaleX, double toScaleY, double toScaleZ, boolean reverse, long durationMs) {
        scaleXYZ.animateStartLoop(toScaleX, toScaleY, toScaleZ, reverse, durationMs);
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStartLoop(new Vector3(toScale, toScale, toScale), reverse, durationMs);</code>
     */
    public ShapeScale animateStartLoop(double toScale, boolean reverse, long durationMs) {
        scaleXYZ.animateStartLoop(toScale, toScale, toScale, reverse, durationMs);
        return this;
    }
}
