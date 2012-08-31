package libshapedraw.primitive;

/**
 * Yet another class representing an (X, Y, Z) vector or coordinate 3-tuple.
 * <p>
 * All modifiers support method chaining, e.g.
 * Vector3 result = new Vector3(2.0, 99.0, 0.0).setY(1.0).addZ(1.0).scaleX(0.5);
 */
public class Vector3 implements ReadonlyVector3 {
    public static ReadonlyVector3 ZEROS = new Vector3(0.0, 0.0, 0.0);

    private double x;
    private double y;
    private double z;

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vector3(ReadonlyVector3 other) {
        this(other.getX(), other.getY(), other.getZ());
    }
    @Override
    public Vector3 copy() {
        return new Vector3(this);
    }
    @Override
    public double getX() {
        return x;
    }
    @Override
    public double getY() {
        return y;
    }
    @Override
    public double getZ() {
        return z;
    }
    @Override
    public double getDistanceSquared(ReadonlyVector3 other) {
        double dx = x - other.getX();
        double dy = y - other.getY();
        double dz = z - other.getZ();
        return dx*dx + dy*dy + dz*dz;
    }
    @Override
    public double getDistance(ReadonlyVector3 other) {
        double dd = getDistanceSquared(other);
        return dd == 0.0 ? 0.0 : Math.sqrt(dd);
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 set(ReadonlyVector3 other) {
        this.x = other.getX();
        this.y = other.getY();
        this.z = other.getZ();
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 setX(double x) {
        this.x = x;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 setY(double y) {
        this.y = y;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 setZ(double z) {
        this.z = z;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 addX(double xOff) {
        x += xOff;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 addY(double yOff) {
        y += yOff;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 addZ(double zOff) {
        z += zOff;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 add(ReadonlyVector3 other) {
        x += other.getX();
        y += other.getY();
        z += other.getZ();
        return this;
    }
    /**
     * Equivalent to add(other.copy().scale(-1.0)).
     * Modifies this vector; does NOT create a copy.
     */
    public Vector3 subtract(ReadonlyVector3 other) {
        x -= other.getX();
        y -= other.getY();
        z -= other.getZ();
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scaleX(double factor) {
        x *= factor;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scaleY(double factor) {
        y *= factor;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scaleZ(double factor) {
        z *= factor;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scale(double factor) {
        x *= factor;
        y *= factor;
        z *= factor;
        return this;
    }

    @Override
    public String toString() {
        return "(" + getX() + "," + getY() + "," + getZ() + ")";
    }
}
