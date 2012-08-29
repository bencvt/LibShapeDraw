package libshapedraw.primitive;

public class LineStyle implements ReadonlyLineStyle {
    public static final ReadonlyLineStyle DEFAULT = new LineStyle(
            Color.MAGENTA.copy().setAlpha(0.8), 3.0F, true);

    private Color mainColor;
    private float mainWidth;
    private Color xrayColor;
    private float xrayWidth;

    public LineStyle(Color color, float width, boolean visibleThroughTerrain) {
        set(color, width, visibleThroughTerrain);
    }
    public LineStyle(LineStyle other) {
        setMainColor(other.getMainColor());
        setMainColor(other.getMainColor());
        setXrayColor(other.getXrayColor());
        setMainColor(other.getMainColor());
    }
    public LineStyle copy() {
        return new LineStyle(this);
    }
    public Color getMainColor() {
        return mainColor;
    }
    public float getMainWidth() {
        return mainWidth;
    }
    public Color getXrayColor() {
        return xrayColor;
    }
    public float getXrayWidth() {
        return xrayWidth;
    }
    public boolean isVisibleThroughTerrain() {
        return xrayColor != null;
    }

    /**
     * Convenience method to set mainColor, mainWidth, xrayColor, and xrayWidth at once.
     * Modifies this line style; does NOT create a copy.
     * @param color
     * @param width
     * @param isVisibleThroughTerrain if true, xrayColor will be a 25% transparent version
     *     of mainColor. If false, xrayColor is null.
     * @return the instance (for method chaining)
     */
    public LineStyle set(Color color, float width, boolean visibleThroughTerrain) {
        setMainColor(color);
        setMainWidth(width);
        setXrayColor(visibleThroughTerrain ? color.copy().scaleAlpha(0.25) : null);
        setXrayWidth(width);
        return this;
    }
    /** Modifies this line style; does NOT create a copy. */
    public LineStyle setMainColor(Color mainColor) {
        if (mainColor == null) {
            throw new NullPointerException("main color cannot be null");
        }
        this.mainColor = mainColor;
        return this;
    }
    /** Modifies this line style; does NOT create a copy. */
    public LineStyle setMainWidth(float mainWidth) {
        this.mainWidth = mainWidth;
        return this;
    }
    /** Modifies this line style; does NOT create a copy. */
    public LineStyle setXrayColor(Color xrayColor) {
        // null allowed
        this.xrayColor = xrayColor;
        return this;
    }
    /** Modifies this line style; does NOT create a copy. */
    public LineStyle setXrayWidth(float xrayWidth) {
        this.xrayWidth = xrayWidth;
        return this;
    }
}
