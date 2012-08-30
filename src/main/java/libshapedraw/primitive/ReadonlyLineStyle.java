package libshapedraw.primitive;

public interface ReadonlyLineStyle {
    public LineStyle copy();
    public LineStyle deepCopy();
    public Color getMainColor();
    public float getMainWidth();
    public Color getSecondaryColor();
    public float getSecondaryWidth();
    public boolean hasSecondaryColor();
}
