package libshapedraw.primitive;

/**
 * Yet another class representing an (X, Y, Z) vector or coordinate 3-tuple.
 * <p>
 * All modifiers support method chaining, e.g.
 * Vector3 result = new Vector3(2.0, 99.0, 0.0).setY(1.0).addZ(1.0).scaleX(0.5);
 */
public class Vector3 implements ReadonlyVector3 {
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
    public Vector3 copy() {
        return new Vector3(this);
    }
    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getZ() {
        return z;
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
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 subtract(ReadonlyVector3 other) {
        x -= other.getX();
        y -= other.getY();
        z -= other.getZ();
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scaleX(double xOff) {
        x *= xOff;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scaleY(double yOff) {
        y *= yOff;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scaleZ(double zOff) {
        z *= zOff;
        return this;
    }
    /** Modifies this vector; does NOT create a copy. */
    public Vector3 scale(ReadonlyVector3 other) {
        x *= other.getX();
        y *= other.getY();
        z *= other.getZ();
        return this;
    }

    @Override
    public String toString() {
        return "(" + getX() + "," + getY() + "," + getZ() + ")";
    }
}
