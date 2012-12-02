package libshapedraw.animation;

/**
 * Simple, easy animation for a set of properties on an object.
 * <p>
 * Calling animateStart/animateStop will take care of setting up and managing
 * an internal {@link libshapedraw.animation.trident.Timeline}, which will
 * update the properties every 40 milliseconds (25 FPS). This interface pares
 * things down to a few simple method calls.
 * <p>
 * For more control over the animation, you can set up your own external
 * Timeline instead. When playing an external Timeline, don't use this
 * interface's methods simultaneously. Two Timelines actively updating the same
 * properties will likely result in unexpected behavior.
 */
public interface Animates<T> {
    /**
     * Whether the properties are being updated by an active animation.
     */
    public boolean isAnimating();

    /**
     * If there is an active animation changing this object's properties, stop
     * it abruptly, leaving things in an intermediate state.
     * <p>
     * After calling this method it is safe to use other methods to modify the
     * properties without being overwritten by the animation.
     * 
     * @return the same object, modified in-place.
     */
    public Animates<T> animateStop();

    /**
     * Start a finite animation on this object's properties, gradually changing
     * them to match the specified properties over the specified interval.
     * <p>
     * After starting an animation, using any other method that modifies the
     * properties is a bad idea, as the animation will be frequently
     * overwriting them. Either wait for the animation to complete or use
     * {@link #animateStop} to halt the animation early.
     * <p>
     * If an animation is already active when this start method is called,
     * animateStop is automatically called before starting the new animation.
     * 
     * @param toProperties the properties to gradually change to.
     * @param durationMs interval in milliseconds, >= 0.
     * @return the same object, modified in-place.
     */
    public Animates<T> animateStart(T toProperties, long durationMs);

    /**
     * Start a looping animation on this object's properties, gradually changing
     * them to match the specified properties over the specified interval.
     * <p>
     * The animation loops indefinitely, going back and forth between the
     * original properties and the specified properties.
     * <p>
     * After starting an animation, using any other method that modifies the
     * properties is a bad idea, as the animation will be frequently
     * overwriting them. Use {@link #animateStop} to halt the animation first.
     * <p>
     * If an animation is already active when this start method is called,
     * animateStop is automatically called before starting the new animation.
     * 
     * @param toProperties the properties to gradually change to.
     * @param reverse if true, fade back to the original properties each time
     *                the animation loops. If false, jump directly back to the
     *                original properties each time.
     * @param durationMs interval in milliseconds of each cycle, >= 0.
     * @return the same object, modified in-place.
     */
    public Animates<T> animateStartLoop(T toProperties, boolean reverse, long durationMs);
}
