package libshapedraw.primitive;

public class LineStyle implements ReadonlyLineStyle {
    public static final ReadonlyLineStyle DEFAULT = new LineStyle(
            Color.MAGENTA.copy().setAlpha(0.8), 3.0F, true);

    private Color mainColor;
    private float mainWidth;
    private Color secondaryColor;
    private float secondaryWidth;

    public LineStyle(Color color, float width, boolean hasSecondaryColor) {
        set(color, width, hasSecondaryColor);
    }
    public LineStyle(LineStyle other) {
        setMainColor(other.getMainColor());
        setMainColor(other.getMainColor());
        setSecondaryColor(other.getSecondaryColor());
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
    public Color getSecondaryColor() {
        return secondaryColor;
    }
    public float getSecondaryWidth() {
        return secondaryWidth;
    }
    public boolean hasSecondaryColor() {
        return secondaryColor != null;
    }

    /**
     * Convenience method to set mainColor, mainWidth, secondaryColor, and secondaryWidth at once.
     * Modifies this line style; does NOT create a copy.
     * @param color
     * @param width
     * @param isVisibleThroughTerrain if true, secondaryColor will be a 25% transparent version
     *     of mainColor. If false, secondaryColor is null.
     * @return the instance (for method chaining)
     */
    public LineStyle set(Color color, float width, boolean visibleThroughTerrain) {
        setMainColor(color);
        setMainWidth(width);
        setSecondaryColor(visibleThroughTerrain ? color.copy().scaleAlpha(0.25) : null);
        setSecondaryWidth(width);
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
    public LineStyle setSecondaryColor(Color secondaryColor) {
        // null allowed
        this.secondaryColor = secondaryColor;
        return this;
    }
    /** Modifies this line style; does NOT create a copy. */
    public LineStyle setSecondaryWidth(float secondaryWidth) {
        this.secondaryWidth = secondaryWidth;
        return this;
    }
}
