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
}
