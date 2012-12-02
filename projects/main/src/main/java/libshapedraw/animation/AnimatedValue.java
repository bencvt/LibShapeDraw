package libshapedraw.animation;

import libshapedraw.animation.trident.Timeline;

/**
 * A wrapper for a single value that can be animated using the Animates
 * interface. Intended to be used for simple scalar values, e.g.:
 * <code>new AnimatedValue<Double>(30.0).animateStart(0.0, 30000);</code>
 */
public class AnimatedValue<T> implements Animates<T> {
    private T value;
    private Timeline timeline;

    public AnimatedValue(T value) {
        setValue(value);
    }

    public T getValue() {
        return value;
    }

    /** Be sure to call animateStop before this method. */
    public AnimatedValue<T> setValue(T value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        this.value = value;
        return this;
    }

    @Override
    public boolean isAnimating() {
        return timeline != null && !timeline.isDone();
    }

    @Override
    public Animates<T> animateStop() {
        if (timeline != null && !timeline.isDone()) {
            timeline.abort();
        }
        timeline = null;
        return this;
    }

    @Override
    public Animates<T> animateStart(T toValue, long durationMs) {
        if (toValue == null) {
            throw new IllegalArgumentException("toValue cannot be null");
        }
        newTimeline(toValue, durationMs);
        timeline.play();
        return this;
    }

    @Override
    public Animates<T> animateStartLoop(T toValue, boolean reverse, long durationMs) {
        if (toValue == null) {
            throw new IllegalArgumentException("toValue cannot be null");
        }
        newTimeline(toValue, durationMs);
        timeline.playLoop(reverse);
        return this;
    }

    private void newTimeline(T toValue, long durationMs) {
        animateStop();
        timeline = new Timeline(this);
        timeline.addPropertyToInterpolate("value", value, toValue);
        timeline.setDuration(durationMs);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
