package libshapedraw.primitive;

import libshapedraw.shape.XrayShape;

import org.lwjgl.opengl.GL11;

/**
 * Represent a line style: a Color and a floating point width.
 * Optionally, the line style can have a secondary Color/width as well.
 * <p>
 * All modifiers support method chaining, e.g.
 * <code>LineStyle result = new LineStyle(Color.WHITE.copy(), 5.0F, true).setSecondaryWidth(2.0F);</code>
 */
public class LineStyle implements ReadonlyLineStyle {
    private static final long serialVersionUID = 1L;
    public static final ReadonlyLineStyle DEFAULT = new LineStyle(
            Color.MAGENTA.copy().setAlpha(0.8), 3.0F, true);

    private Color mainColor;
    private float mainWidth;
    private Color secondaryColor;
    private float secondaryWidth;

    public LineStyle(Color color, float width, boolean hasSecondaryColor) {
        set(color, width, hasSecondaryColor);
    }

    public LineStyle(Color mainColor, float mainWidth, Color secondaryColor, float secondaryWidth) {
        set(mainColor, mainWidth, secondaryColor, secondaryWidth);
    }

    public LineStyle(ReadonlyLineStyle other) {
        setMainColor(other.getMainReadonlyColor().copy());
        setMainWidth(other.getMainWidth());
        setSecondaryColor(other.getSecondaryReadonlyColor() == null ? null : other.getSecondaryReadonlyColor().copy());
        setSecondaryWidth(other.getSecondaryWidth());
    }

    @Override
    public LineStyle copy() {
        return new LineStyle(this);
    }

    @Override
    public ReadonlyColor getMainReadonlyColor() {
        return mainColor;
    }

    @Override
    public ReadonlyColor getSecondaryReadonlyColor() {
        return secondaryColor;
    }

    @Override
    public float getMainWidth() {
        return mainWidth;
    }

    @Override
    public float getSecondaryWidth() {
        return secondaryWidth;
    }

    @Override
    public boolean hasSecondaryColor() {
        return secondaryColor != null;
    }

    @Override
    public boolean glApply(boolean useSecondary) {
        if (useSecondary) {
            if (secondaryColor == null) {
                return false;
            }
            GL11.glDepthFunc(GL11.GL_GREATER);
            secondaryColor.glApply();
            GL11.glLineWidth(secondaryWidth);
        } else {
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            mainColor.glApply();
            GL11.glLineWidth(mainWidth);
        }
        return true;
    }

    @Override
    public boolean isAnimating() {
        return mainColor.isAnimating() ||
                (secondaryColor != null && secondaryColor.isAnimating());
    }

    /** @return true if two line styles are equal. */
    @Override
    public boolean equals(Object other) {
        return other instanceof LineStyle && toString().equals(other.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
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

    // ========
    // Accessors for mutable properties
    // ========

    /** @return the main line color, mutable. */
    public Color getMainColor() {
        return mainColor;
    }

    /** @return the secondary line color, mutable. May be null. */
    public Color getSecondaryColor() {
        return secondaryColor;
    }

    // ========
    // Mutators
    // ========

    /**
     * Convenience method to set mainColor, mainWidth, secondaryColor, and
     * secondaryWidth at once.
     * @param color sets mainColor
     * @param width sets both mainWidth and secondaryWidth
     * @param hasSecondaryColor if true, secondaryColor will be a
     *        semi-transparent version of mainColor. If false, secondaryColor
     *        is null.
     * @return the same line style object, modified in-place.
     */
    public LineStyle set(Color color, float width, boolean hasSecondaryColor) {
        setMainColor(color);
        setMainWidth(width);
        if (hasSecondaryColor) {
            setSecondaryColorFromMain();
        } else {
            setSecondaryColor(null);
        }
        setSecondaryWidth(width);
        return this;
    }

    /**
     * Set all components of this line style.
     * @return the same line style object, modified in-place.
     */
    public LineStyle set(Color mainColor, float mainWidth, Color secondaryColor, float secondaryWidth) {
        setMainColor(mainColor);
        setMainWidth(mainWidth);
        setSecondaryColor(secondaryColor);
        setSecondaryWidth(secondaryWidth);
        return this;
    }

    /**
     * Set the line style's main color, which cannot be null.
     * @return the same line style object, modified in-place.
     */
    public LineStyle setMainColor(Color mainColor) {
        if (mainColor == null) {
            throw new IllegalArgumentException("main color cannot be null");
        }
        this.mainColor = mainColor;
        return this;
    }

    /**
     * Set the line style's main width.
     * @return the same line style object, modified in-place.
     */
    public LineStyle setMainWidth(float mainWidth) {
        if (mainWidth < 0) {
            throw new IllegalArgumentException("line width must be positive");
        }
        this.mainWidth = mainWidth;
        return this;
    }

    /**
     * Set the line style's secondary color, which can be null.
     * @return the same line style object, modified in-place.
     */
    public LineStyle setSecondaryColor(Color secondaryColor) {
        // null allowed
        this.secondaryColor = secondaryColor;
        return this;
    }

    /**
     * Set the line style's secondary color to a semi-transparent version of
     * the main line color.
     * @return the same line style object, modified in-place.
     */
    public LineStyle setSecondaryColorFromMain() {
        secondaryColor = mainColor.copy().scaleAlpha(XrayShape.SECONDARY_ALPHA);
        return this;
    }

    /**
     * Set the line style's secondary width.
     * @return the same line style object, modified in-place.
     */
    public LineStyle setSecondaryWidth(float secondaryWidth) {
        if (secondaryWidth < 0) {
            throw new IllegalArgumentException("line width must be positive");
        }
        this.secondaryWidth = secondaryWidth;
        return this;
    }
}
