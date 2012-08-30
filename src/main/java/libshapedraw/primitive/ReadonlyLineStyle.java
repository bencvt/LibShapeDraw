package libshapedraw.primitive;

public interface ReadonlyLineStyle {
    public LineStyle copy();
    public ReadonlyColor getMainReadonlyColor();
    public float getMainWidth();
    public ReadonlyColor getSecondaryReadonlyColor();
    public float getSecondaryWidth();
    public boolean hasSecondaryColor();
}
