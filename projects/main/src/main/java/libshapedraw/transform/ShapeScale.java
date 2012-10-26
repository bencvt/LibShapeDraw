package libshapedraw.transform;

import libshapedraw.animation.trident.Timeline;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * Resize a Shape using glScale.
 */
public class ShapeScale implements ShapeTransform {
    private Vector3 scaleXYZ;
    private Timeline timeline;

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
            throw new IllegalArgumentException();
        }
        this.scaleXYZ = scaleXYZ;
        return this;
    }

    @Override
    public void preRender() {
        GL11.glScaled(scaleXYZ.getX(), scaleXYZ.getY(), scaleXYZ.getZ());
    }

    /**
     * @return true if the scale vector is being updated by an active
     *         animation.
     */
    public boolean isAnimating() {
        return timeline != null && !timeline.isDone();
    }

    /**
     * If there is an active animation changing the scale vector's components,
     * stop it abruptly, leaving the components in an intermediate state.
     * <p>
     * After calling this method it is safe to use other methods to modify the
     * vector without being overwritten by the animation.
     * 
     * @return the same ShapeScale object, modified in-place.
     */
    public ShapeScale animateStop() {
        if (timeline != null && !timeline.isDone()) {
            timeline.abort();
            timeline = null;
        }
        return this;
    }

    /**
     * Convenience method, equivalent to:
     * animateStart(new Vector3(toScaleX, toScaleY, toScaleZ), durationMs);
     */
    public ShapeScale animateStart(double toScaleX, double toScaleY, double toScaleZ, long durationMs) {
        newTimeline(toScaleX, toScaleY, toScaleZ, durationMs);
        timeline.play();
        return this;
    }
    /**
     * Convenience method, equivalent to:
     * animateStart(new Vector3(toScale, toScale, toScale), durationMs);
     */
    public ShapeScale animateStart(double toScale, long durationMs) {
        return animateStart(toScale, toScale, toScale, durationMs);
    }
    /**
     * Animate the ShapeScale, resizing the target Shape(s).
     * <p>
     * After starting an animation, using other methods to modify the scale
     * vector's components is a bad idea, as the animation will be frequently
     * overwriting them. Either wait for the animation to complete or use
     * {@link #animateStop} to halt the animation early.
     * <p>
     * This is a convenience method; for more control over the animation,
     * an external Timeline can be used. Just don't have two Timelines trying
     * to update the same properties.
     * 
     * @param toScale the size to animate to
     * @param durationMs interval in milliseconds
     * @return the same ShapeScale object, modified in-place.
     */
    public ShapeScale animateStart(ReadonlyVector3 toScale, long durationMs) {
        return animateStart(toScale.getX(), toScale.getY(), toScale.getZ(), durationMs);
    }

    /**
     * Convenience method, equivalent to:
     * animateStartLoop(new Vector3(toScaleX, toScaleY, toScaleZ), reverse, durationMs);
     */
    public ShapeScale animateStartLoop(double toScaleX, double toScaleY, double toScaleZ, boolean reverse, long durationMs) {
        newTimeline(toScaleX, toScaleY, toScaleZ, durationMs);
        timeline.playLoop(reverse);
        return this;
    }
    /**
     * Convenience method, equivalent to:
     * animateStartLoop(new Vector3(toScale, toScale, toScale), reverse, durationMs);
     */
    public ShapeScale animateStartLoop(double toScale, boolean reverse, long durationMs) {
        return animateStartLoop(toScale, toScale, toScale, reverse, durationMs);
    }
    /**
     * Animate the ShapeScale, resizing the target Shape(s).
     * The animation loops indefinitely, going back and forth between the
     * original scale vector and the specified size.
     * <p>
     * After starting an animation, using other methods to modify the scale
     * vector's components is a bad idea, as the animation will be frequently
     * overwriting them. Use {@link #animateStop} to halt the animation.
     * <p>
     * This is a convenience method; for more control over the animation,
     * an external Timeline can be used. Just don't have two Timelines trying
     * to update the same properties.
     * 
     * @param toScale the size to animate to
     * @param reverse if true, fade back down to the original size each time
     *        the animation loops. If false, jump back to the original size
     *        immediately each time.
     * @param durationMs interval in milliseconds
     * @return the same ShapeScale object, modified in-place.
     */
    public ShapeScale animateStartLoop(ReadonlyVector3 toScale, boolean reverse, long durationMs) {
        return animateStartLoop(toScale.getX(), toScale.getY(), toScale.getZ(), reverse, durationMs);
    }

    private void newTimeline(double toScaleX, double toScaleY, double toScaleZ, long durationMs) {
        animateStop();
        timeline = new Timeline(scaleXYZ);
        timeline.addPropertyToInterpolate("x", scaleXYZ.getX(), toScaleX);
        timeline.addPropertyToInterpolate("y", scaleXYZ.getY(), toScaleY);
        timeline.addPropertyToInterpolate("z", scaleXYZ.getZ(), toScaleZ);
        timeline.setDuration(durationMs);
    }
}
