package libshapedraw.primitive;

public interface ReadonlyColor {
    public Color copy();
    public double getRed();
    public double getGreen();
    public double getBlue();
    public double getAlpha();
    public int getRGBA();
}
