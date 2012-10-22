package libshapedraw.primitive;

public interface ReadonlyColor {
    /** @return a new deep-copied mutable Color. */
    public Color copy();

    public double getRed();

    public double getGreen();

    public double getBlue();

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
     * Convenience method, equivalent to:
     * GL11.glColor4d(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
     */
    public void glApply();

    /**
     * Convenience method similar to {@link #glApply} that also applies an
     * alpha scaling factor. The final alpha passed to OpenGL is clamped to
     * [0.0, 1.0]. The Color instance remains unchanged.
     * <p>
     * Equivalent to c.copy().scaleAlpha(alphaScale).glApply(), without the
     * extra Color instance creation.
     */
    public void glApply(double alphaScale);
}
