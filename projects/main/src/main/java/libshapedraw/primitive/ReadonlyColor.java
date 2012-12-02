package libshapedraw.primitive;

import java.io.Serializable;

/**
 * Read-only interface for Color objects, allowing for compile-time safety.
 * @see Color
 */
public interface ReadonlyColor extends Serializable {
    /**
     * @return a new deep-copied mutable Color. Does not copy animations.
     *         <p>
     *         Same concept as Object.clone(), minus the tedious/clunky checked
     *         exception, CloneNotSupportedException.
     */
    public Color copy();

    /** @return this color's red component, in the range [0.0, 1.0]. */
    public double getRed();

    /** @return this color's green component, in the range [0.0, 1.0]. */
    public double getGreen();

    /** @return this color's blue component, in the range [0.0, 1.0]. */
    public double getBlue();

    /**
     * @return this color's alpha component, in the range [0.0, 1.0].
     *         0.0 is fully transparent; 1.0 is fully opaque.
     */
    public double getAlpha();

    /**
     * @return a 32-bit integer packed with each of the color's components, one
     *         per byte. The order, from most significant to least, is
     *         red, green, blue, alpha.
     */
    public int getRGBA();

    /**
     * @return a 32-bit integer packed with each of the color's components, one
     *         per byte. The order, from most significant to least, is
     *         alpha, red, green, blue.
     */
    public int getARGB();

    /**
     * Convenience method to update the OpenGL state to use this color.
     * <p>
     * Equivalent to
     * <code>GL11.glColor4d(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha())</code>
     */
    public void glApply();

    /**
     * Convenience method to update the OpenGL state to use this color, with
     * the alpha value scaled by a specified factor. The Color instance remains
     * unchanged by the factor. The final alpha value passed to OpenGL is
     * clamped to [0.0, 1.0]. 
     * <p>
     * Equivalent to <code>c.copy().scaleAlpha(alphaScale).glApply()</code>,
     * without the extra Color instance creation.
     */
    public void glApply(double alphaScale);

    /** @return true if this color is being updated by an active animation. */
    public boolean isAnimating();

    @Override
    public boolean equals(Object other);

    @Override
    public int hashCode();

    @Override
    public String toString();
}
