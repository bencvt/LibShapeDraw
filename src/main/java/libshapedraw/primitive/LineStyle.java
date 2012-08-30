package libshapedraw.primitive;

/**
 * Represent a line style: a Color and a floating point width.
 * Optionally, the line style can have a secondary Color/width as well.
 * <p>
 * All modifiers support method chaining, e.g.
 * LineStyle result = new LineStyle(Color.WHITE.copy(), 5.0F, true).setSecondaryWidth(2.0F);
 */
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
    public LineStyle(ReadonlyLineStyle other) {
        setMainColor(other.getMainReadonlyColor().copy());
        setMainWidth(other.getMainWidth());
        setSecondaryColor(other.getSecondaryReadonlyColor() == null ? null : other.getSecondaryReadonlyColor().copy());
        setSecondaryWidth(other.getSecondaryWidth());
    }
    public LineStyle copy() {
        return new LineStyle(this);
    }
    public ReadonlyColor getMainReadonlyColor() {
        return mainColor;
    }
    public ReadonlyColor getSecondaryReadonlyColor() {
        return secondaryColor;
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
     * @param hasSecondaryColor if true, secondaryColor will be a 25% transparent version
     *     of mainColor. If false, secondaryColor is null.
     * @return the instance (for method chaining)
     */
    public LineStyle set(Color color, float width, boolean hasSecondaryColor) {
        setMainColor(color);
        setMainWidth(width);
        setSecondaryColor(hasSecondaryColor ? color.copy().scaleAlpha(0.25) : null);
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
        if (mainWidth < 0) {
            throw new IllegalArgumentException("line width must be positive");
        }
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
        if (secondaryWidth < 0) {
            throw new IllegalArgumentException("line width must be positive");
        }
        this.secondaryWidth = secondaryWidth;
        return this;
    }
    @Override
    public String toString() {
        StringBuilder b = new StringBuilder("(");
        b.append(getMainColor()).append(',').append(getMainWidth());
        if (hasSecondaryColor()) {
            b.append('|').append(getSecondaryColor()).append(',').append(getSecondaryWidth());
        }
        return b.append(')').toString();
    }
}
