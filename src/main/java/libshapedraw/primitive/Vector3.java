package libshapedraw.primitive;

/**
 * Yet another class representing a (X, Y, Z) vector or coordinate 3-tuple.
 * <p>
 * All modifiers support method chaining, e.g.
 * Vector3 result = new Vector3(2.0, 99.0, 0.0).setY(1.0).addZ(1.0).scaleX(0.5);
 */
public class Vector3 implements ReadonlyVector3 {
    public static final ReadonlyVector3 ZEROS = new Vector3(0.0, 0.0, 0.0);

    private static final double R2D = 180.0 / Math.PI;
    private static final double D2R = Math.PI / 180.0;

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
        return new Vector3(x, y, z);
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
    public boolean isZero() {
        return x == 0.0 && y == 0.0 && z == 0.0;
    }

    @Override
    public double lengthSquared() {
        return Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
    }

    @Override
    public double length() {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    @Override
    public double distanceSquared(ReadonlyVector3 other) {
        return Math.pow(x - other.getX(), 2) + Math.pow(y - other.getY(), 2) + Math.pow(z - other.getZ(), 2);
    }

    @Override
    @Deprecated public double getDistanceSquared(ReadonlyVector3 other) {
        return Math.pow(x - other.getX(), 2) + Math.pow(y - other.getY(), 2) + Math.pow(z - other.getZ(), 2);
    }

    @Override
    public double distance(ReadonlyVector3 other) {
        return Math.sqrt(Math.pow(x - other.getX(), 2) + Math.pow(y - other.getY(), 2) + Math.pow(z - other.getZ(), 2));
    }

    @Override
    @Deprecated public double getDistance(ReadonlyVector3 other) {
        return Math.sqrt(Math.pow(x - other.getX(), 2) + Math.pow(y - other.getY(), 2) + Math.pow(z - other.getZ(), 2));
    }

    @Override
    public double dot(ReadonlyVector3 other) {
        return x*other.getX() + y*other.getY() + z*other.getZ();
    }

    @Override
    public double angle(ReadonlyVector3 other) {
        double angle = Math.acos(dot(other) / (length() * other.length()));
        if (Double.isNaN(angle)) {
            if (x != 0.0 && Math.signum(x) == -Math.signum(other.getX())) {
                // vectors point in exact opposite directions
                return Math.PI;
            } else {
                // same direction or at least one of the vectors is all zeros
                return 0.0;
            }
        } else {
            return angle;
        }
    }

    @Override
    public double angleDegrees(ReadonlyVector3 other) {
        return angle(other) * R2D;
    }

    @Override
    public double yaw() {
        return Math.atan2(x, z);
    }

    @Override
    public double yawDegrees() {
        return Math.atan2(x, z) * R2D;
    }

    @Override
    public double pitch() {
        double m = Math.pow(x, 2) + Math.pow(z, 2);
        if (m > 0.0) {
            return Math.asin(y / Math.sqrt(m));
        } else if (y < 0.0) {
            // straight up
            return -Math.PI / 2.0;
        } else if (y > 0.0) {
            // straight down
            return Math.PI / 2.0;
        } else {
            // vector is all zeros
            return 0.0;
        }
    }

    @Override
    public double pitchDegrees() {
        return pitch() * R2D;
    }

    @Override
    public boolean isInAABB(ReadonlyVector3 lowerCorner, ReadonlyVector3 upperCorner) {
        if (lowerCorner.getX() > upperCorner.getX() ||
                lowerCorner.getY() > upperCorner.getY() ||
                lowerCorner.getZ() > upperCorner.getZ()) {
            return (x >= Math.min(lowerCorner.getX(), upperCorner.getX()) &&
                    x <= Math.max(lowerCorner.getX(), upperCorner.getX()) &&
                    y >= Math.min(lowerCorner.getY(), upperCorner.getY()) &&
                    y <= Math.max(lowerCorner.getY(), upperCorner.getY()) &&
                    z >= Math.min(lowerCorner.getZ(), upperCorner.getZ()) &&
                    z <= Math.max(lowerCorner.getZ(), upperCorner.getZ()));
        }
        return (x >= lowerCorner.getX() &&
                x <= upperCorner.getX() &&
                y >= lowerCorner.getY() &&
                y <= upperCorner.getY() &&
                z >= lowerCorner.getZ() &&
                z <= upperCorner.getZ());
    }

    @Override
    public boolean isInSphere(ReadonlyVector3 origin, double radius) {
        return (Math.pow(x - origin.getX(), 2) + Math.pow(y - origin.getY(), 2) + Math.pow(z - origin.getZ(), 2)) <= Math.pow(radius, 2);
    }

    @Override
    public String toString() {
        return "(" + getX() + "," + getY() + "," + getZ() + ")";
    }

    // ========
    // Mutators
    // ========

    /**
     * Set all of this vector's components to match another vector's.
     * @return the same vector object, modified in-place.
     */
    public Vector3 set(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Set all of this vector's components to match another vector's.
     * @return the same vector object, modified in-place.
     */
    public Vector3 set(ReadonlyVector3 other) {
        x = other.getX();
        y = other.getY();
        z = other.getZ();
        return this;
    }

    /**
     * Set this vector's x component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Set this vector's y component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setY(double y) {
        this.y = y;
        return this;
    }

    /**
     * Set this vector's z component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Add to this vector's x component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 addX(double xOff) {
        x += xOff;
        return this;
    }

    /**
     * Add to this vector's y component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 addY(double yOff) {
        y += yOff;
        return this;
    }

    /**
     * Add to this vector's z component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 addZ(double zOff) {
        z += zOff;
        return this;
    }

    /**
     * Add a vector to this vector.
     * @return the same vector object, modified in-place.
     */
    public Vector3 add(double xOff, double yOff, double zOff) {
        x += xOff;
        y += yOff;
        z += zOff;
        return this;
    }

    /**
     * Add a vector to this vector.
     * @return the same vector object, modified in-place.
     */
    public Vector3 add(ReadonlyVector3 other) {
        x += other.getX();
        y += other.getY();
        z += other.getZ();
        return this;
    }

    /**
     * Subtract a vector from this vector.
     * Equivalent to addX(-other.getX()).addY(-other.getY()).addZ(-other.getZ()).
     * @return the same vector object, modified in-place.
     */
    public Vector3 subtract(ReadonlyVector3 other) {
        x -= other.getX();
        y -= other.getY();
        z -= other.getZ();
        return this;
    }

    /**
     * Set all of this vector's components to 0.
     * Equivalent to set(Vector3.ZEROS).
     * @return the same vector object, modified in-place.
     */
    public Vector3 zero() {
        this.x = 0.0;
        this.y = 0.0;
        this.z = 0.0;
        return this;
    }

    /**
     * Set each of this vector's components to a random value in [0.0, 1.0).
     * @return the same vector object, modified in-place.
     */
    public Vector3 setRandom() {
        x = Math.random();
        y = Math.random();
        z = Math.random();
        return this;
    }

    /**
     * Given two other vectors, pick the lowest x, y, and z values and set this
     * vector's components to those values.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setMinimum(ReadonlyVector3 a, ReadonlyVector3 b) {
        x = Math.min(a.getX(), b.getX());
        y = Math.min(a.getY(), b.getY());
        z = Math.min(a.getZ(), b.getZ());
        return this;
    }

    /**
     * Given two other vectors, pick the highest x, y, and z values and set this
     * vector's components to those values.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setMaximum(ReadonlyVector3 a, ReadonlyVector3 b) {
        x = Math.max(a.getX(), b.getX());
        y = Math.max(a.getY(), b.getY());
        z = Math.max(a.getZ(), b.getZ());
        return this;
    }

    /**
     * Set this vector's to a unit vector (i.e. length=1) facing the direction
     * specified by yaw and pitch angles, both in radians.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setFromYawPitch(double yaw, double pitch) {
        x = Math.sin(yaw)*Math.cos(pitch);
        y = Math.sin(pitch);
        z = Math.cos(yaw)*Math.cos(pitch);
        return this;
    }

    /**
     * Set this vector's to a unit vector (i.e. length=1) facing the direction
     * specified by yaw and pitch angles, both in degrees.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setFromYawPitchDegrees(double yawDegrees, double pitchDegrees) {
        return setFromYawPitch(yawDegrees*D2R, pitchDegrees*D2R);
    }

    /**
     * Multiply each component of this vector by a given factor.
     * @return the same vector object, modified in-place.
     */
    public Vector3 scale(double factor) {
        x *= factor;
        y *= factor;
        z *= factor;
        return this;
    }

    /**
     * Multiply the x component of this vector by a given factor.
     * @return the same vector object, modified in-place.
     */
    public Vector3 scaleX(double factor) {
        x *= factor;
        return this;
    }

    /**
     * Multiply the y component of this vector by a given factor.
     * @return the same vector object, modified in-place.
     */
    public Vector3 scaleY(double factor) {
        y *= factor;
        return this;
    }

    /**
     * Multiply the z component of this vector by a given factor.
     * @return the same vector object, modified in-place.
     */
    public Vector3 scaleZ(double factor) {
        z *= factor;
        return this;
    }

    /**
     * Set each component of this vector to its negative value.
     * Equivalent to scale(-1).
     * @return the same vector object, modified in-place.
     */
    public Vector3 negate() {
        x = -x;
        y = -y;
        z = -z;
        return this;
    }

    /**
     * Set each component of this vector to its absolute value.
     * @return the same vector object, modified in-place.
     */
    public Vector3 absolute() {
        x = Math.abs(x);
        y = Math.abs(y);
        z = Math.abs(z);
        return this;
    }

    /**
     * Ensure that each component of this vector is in the specified range.
     * @return the same vector object, modified in-place.
     */
    public Vector3 clamp(double min, double max) {
        x = Math.min(max, Math.max(min, x));
        y = Math.min(max, Math.max(min, y));
        z = Math.min(max, Math.max(min, z));
        return this;
    }

    /**
     * Ensure that the x component of this vector is in the specified range.
     * @return the same vector object, modified in-place.
     */
    public Vector3 clampX(double min, double max) {
        x = Math.min(max, Math.max(min, x));
        return this;
    }

    /**
     * Ensure that the y component of this vector is in the specified range.
     * @return the same vector object, modified in-place.
     */
    public Vector3 clampY(double min, double max) {
        y = Math.min(max, Math.max(min, y));
        return this;
    }

    /**
     * Ensure that the z component of this vector is in the specified range.
     * @return the same vector object, modified in-place.
     */
    public Vector3 clampZ(double min, double max) {
        z = Math.min(max, Math.max(min, z));
        return this;
    }

    /**
     * Set this vector to the midpoint in between it and another vector.
     * Equivalent to interpolate(other, 0.5).
     * @return the same vector object, modified in-place.
     */
    public Vector3 midpoint(ReadonlyVector3 other) {
        x = x/2.0 + other.getX()/2.0;
        y = y/2.0 + other.getY()/2.0;
        z = z/2.0 + other.getZ()/2.0;
        return this;
    }

    /**
     * Set this vector to a point along the line between it and another vector.
     * E.g., 0.0 is this vector; 0.5 is halfway between this vector and the
     * other; 1.0 is the other vector.
     * @return the same vector object, modified in-place.
     */
    public Vector3 interpolate(ReadonlyVector3 other, double alpha) {
        x = (1.0 - alpha)*x + alpha*other.getX();
        y = (1.0 - alpha)*y + alpha*other.getY();
        z = (1.0 - alpha)*z + alpha*other.getZ();
        return this;
    }

    /**
     * Set this vector to the cross product between this vector and another.
     * @return the same vector object, modified in-place.
     */
    public Vector3 cross(ReadonlyVector3 other) {
        double tmpX = y*other.getZ() - z*other.getY();
        double tmpY = z*other.getX() - x*other.getZ();
        z           = x*other.getY() - y*other.getX();
        x = tmpX;
        y = tmpY;
        return this;
    }

    /**
     * Convert this vector to a unit vector. I.e., a length of 1 but still
     * facing in the same direction. If the vector is all zeros, it is left
     * unchanged.
     * @return the same vector object, modified in-place.
     */
    public Vector3 normalize() {
        double length = length();
        if (length == 0.0) {
            return this;
        }
        x /= length;
        y /= length;
        z /= length;
        return this;
    }
}
