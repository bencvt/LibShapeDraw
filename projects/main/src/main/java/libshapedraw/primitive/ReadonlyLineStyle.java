package libshapedraw.primitive;

/**
 * Read-only interface for LineStyle objects, allowing for compile-time safety.
 * @see LineStyle
 */
public interface ReadonlyLineStyle {
    /** @return a new deep-copied mutable LineStyle. */
    public LineStyle copy();

    /** @return a read-only view of the main line color. */
    public ReadonlyColor getMainReadonlyColor();

    /** @return the main line width. */
    public float getMainWidth();

    /** @return a read-only view of the secondary line color. May be null. */
    public ReadonlyColor getSecondaryReadonlyColor();

    /**
     * @return the secondary line width, which is unused if the secondary line
     *         color is null.
     */
    public float getSecondaryWidth();

    /** @return true if the secondary line color is null. */
    public boolean hasSecondaryColor();

    /**
     * Convenience method that sets the OpenGL state to match this line style.
     * I.e., call glColor4d, glLineWidth, and glDepthFunc.
     * @return false if the OpenGL state was not set due to the secondary color
     *         not being set for this line style.
     */
    public boolean glApply(boolean useSecondary);

    /**
     * @return true if either of this line style's colors is being updated by
     *         an active animation.
     *         <p>
     *         The line widths can be animated by a Timeline too, but not an
     *         internal one: LineStyle does not implement the full Animateable
     *         interface.
     */
    public boolean isAnimating();

    @Override
    public boolean equals(Object other);

    @Override
    public int hashCode();

    @Override
    public String toString();
}
