package libshapedraw.primitive;

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
}
