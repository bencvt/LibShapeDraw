package libshapedraw.primitive;

public interface ReadonlyLineStyle {
    public LineStyle copy();
    public Color getMainColor();
    public float getMainWidth();
    public Color getXrayColor();
    public float getXrayWidth();
    public boolean isVisibleThroughTerrain();
}
