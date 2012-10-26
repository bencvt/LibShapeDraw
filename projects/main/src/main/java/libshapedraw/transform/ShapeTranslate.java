package libshapedraw.transform;

import libshapedraw.animation.trident.Timeline;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * Adjust the x/y/z coordinates of a Shape using glTranslate.
 */
public class ShapeTranslate implements ShapeTransform {
    private Vector3 translateXYZ;
    private Timeline timeline;

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
            throw new IllegalArgumentException();
        }
        this.translateXYZ = translateXYZ;
        return this;
    }

    @Override
    public void preRender() {
        GL11.glTranslated(translateXYZ.getX(), translateXYZ.getY(), translateXYZ.getZ());
    }

    /**
     * @return true if the translate vector is being updated by an active
     *         animation.
     */
    public boolean isAnimating() {
        return timeline != null && !timeline.isDone();
    }

    /**
     * If there is an active animation changing the translate vector's
     * components, stop it abruptly, leaving the components in an intermediate
     * state.
     * <p>
     * After calling this method it is safe to use other methods to modify the
     * vector without being overwritten by the animation.
     * 
     * @return the same ShapeTranslate object, modified in-place.
     */
    public ShapeTranslate animateStop() {
        if (timeline != null && !timeline.isDone()) {
            timeline.abort();
            timeline = null;
        }
        return this;
    }

    /**
     * Convenience method, equivalent to:
     * animateStart(new Vector3(toTranslateX, toTranslateY, toTranslateZ), durationMs);
     */
    public ShapeTranslate animateStart(double toTranslateX, double toTranslateY, double toTranslateZ, long durationMs) {
        newTimeline(toTranslateX, toTranslateY, toTranslateZ, durationMs);
        timeline.play();
        return this;
    }
    /**
     * Animate the ShapeTranslate, moving the target Shape(s).
     * <p>
     * After starting an animation, using other methods to modify the translate
     * vector's components is a bad idea, as the animation will be frequently
     * overwriting them. Either wait for the animation to complete or use
     * {@link #animateStop} to halt the animation early.
     * <p>
     * This is a convenience method; for more control over the animation,
     * an external Timeline can be used. Just don't have two Timelines trying
     * to update the same properties.
     * 
     * @param toTranslate the position adjustment to animate to
     * @param durationMs interval in milliseconds
     * @return the same ShapeTranslate object, modified in-place.
     */
    public ShapeTranslate animateStart(ReadonlyVector3 toTranslate, long durationMs) {
        return animateStart(toTranslate.getX(), toTranslate.getY(), toTranslate.getZ(), durationMs);
    }

    /**
     * Convenience method, equivalent to:
     * animateStart(new Vector3(toTranslateX, toTranslateY, toTranslateZ), reverse, durationMs);
     */
    public ShapeTranslate animateStartLoop(double toTranslateX, double toTranslateY, double toTranslateZ, boolean reverse, long durationMs) {
        newTimeline(toTranslateX, toTranslateY, toTranslateZ, durationMs);
        timeline.playLoop(reverse);
        return this;
    }
    /**
     * Animate the ShapeTranslate, moving the target Shape(s).
     * The animation loops indefinitely, going back and forth between the
     * original translate vector and the specified one.
     * <p>
     * After starting an animation, using other methods to modify the translate
     * vector's components is a bad idea, as the animation will be frequently
     * overwriting them. Use {@link #animateStop} to halt the animation.
     * <p>
     * This is a convenience method; for more control over the animation,
     * an external Timeline can be used. Just don't have two Timelines trying
     * to update the same properties.
     * 
     * @param toTranslate the position adjustment to animate to
     * @param reverse if true, fade back down to the original position each
     *        time the animation loops. If false, jump back to the original
     *        position immediately each time.
     * @param durationMs interval in milliseconds
     * @return the same ShapeTranslate object, modified in-place.
     */
    public ShapeTranslate animateStartLoop(ReadonlyVector3 toTranslate, boolean reverse, long durationMs) {
        return animateStartLoop(toTranslate.getX(), toTranslate.getY(), toTranslate.getZ(), reverse, durationMs);
    }

    private void newTimeline(double toTranslateX, double toTranslateY, double toTranslateZ, long durationMs) {
        animateStop();
        timeline = new Timeline(translateXYZ);
        timeline.addPropertyToInterpolate("x", translateXYZ.getX(), toTranslateX);
        timeline.addPropertyToInterpolate("y", translateXYZ.getY(), toTranslateY);
        timeline.addPropertyToInterpolate("z", translateXYZ.getZ(), toTranslateY);
        timeline.setDuration(durationMs);
    }
}
