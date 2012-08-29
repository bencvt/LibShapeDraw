package libshapedraw.primitive;

public interface ReadonlyVector3 {
    public Vector3 copy();
    public double getX();
    public double getY();
    public double getZ();
    public double getDistanceSquared(ReadonlyVector3 other);
    public double getDistance(ReadonlyVector3 other);
}
