package libshapedraw.primitive;

import java.io.Serializable;

import org.lwjgl.opengl.GL11;

import libshapedraw.animation.Animates;
import libshapedraw.animation.trident.Timeline;

/**
 * Yet another class representing a (X, Y, Z) vector or coordinate 3-tuple.
 * <p>
 * All modifiers support method chaining, e.g.
 * <code>Vector3 result = new Vector3(2.0, 99.0, 0.0).setY(1.0).addZ(1.0).scaleX(0.5);</code>
 */
public class Vector3 implements ReadonlyVector3, Animates<ReadonlyVector3>, Serializable {
    private static final long serialVersionUID = 1L;
    public static final ReadonlyVector3 ZEROS = new Vector3();

    private static final double R2D = 180.0 / Math.PI;
    private static final double D2R = Math.PI / 180.0;

    private double x;
    private double y;
    private double z;

    /**
     * It is perhaps a bit wasteful to have a Timeline field on every Vector3
     * instance just to support those few Vector3s that need to be animated,
     * even if it is lazily instantiated. However, developer convenience wins
     * over premature optimization.
     * <p>
     * In any case, java.nio.FloatBuffer is a more appropriate data container
     * for a large number of vertices in a memory-constrained environment.
     */
    private transient Timeline timeline;

    /** Create a new vector with all components set to zero. */
    public Vector3() {
        // do nothing; 0.0 is the default double value already
    }

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
    public double getComponent(Axis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("axis cannot be null");
        } else if (axis == Axis.X) {
            return x;
        } else if (axis == Axis.Y) {
            return y;
        } else {
            return z;
        }
    }

    @Override
    public boolean componentEquals(Axis axis, double value, double epsilon) {
        // A negative epsilon is pointless (causing this check to always
        // return false), but still valid.
        return Math.abs(getComponent(axis) - value) <= epsilon;
    }

    @Override
    public boolean equalsExact(ReadonlyVector3 other) {
        return other != null && equalsExact(other.getX(), other.getY(), other.getZ());
    }

    @Override
    public boolean equalsExact(double otherX, double otherY, double otherZ) {
        return x == otherX && y == otherY && z == otherZ;
    }

    @Override
    public boolean equals(ReadonlyVector3 other, double epsilon) {
        return other != null && equals(other.getX(), other.getY(), other.getZ(), epsilon);
    }

    @Override
    public boolean equals(double otherX, double otherY, double otherZ, double epsilon) {
        // A negative epsilon is pointless (causing this check to always
        // return false), but still valid.
        return (Math.abs(x - otherX) <= epsilon &&
                Math.abs(y - otherY) <= epsilon &&
                Math.abs(z - otherZ) <= epsilon);
    }

    @Deprecated
    @Override
    public boolean equals(Object other) {
        return other instanceof ReadonlyVector3 && equalsExact((ReadonlyVector3) other);
    }

    @Override
    public int hashCode() {
        // Equivalent to java.util.Arrays.hashCode(new double[] {x, y, z})
        // without the extra object allocation.
        long bits;
        int hash = 1;
        bits = Double.doubleToLongBits(x);
        hash = 31*hash + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(y);
        hash = 31*hash + (int) (bits ^ (bits >>> 32));
        bits = Double.doubleToLongBits(z);
        hash = 31*hash + (int) (bits ^ (bits >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + "," + z + ")";
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
        return  Math.pow(x - origin.getX(), 2) +
                Math.pow(y - origin.getY(), 2) +
                Math.pow(z - origin.getZ(), 2)
                <= Math.pow(radius, 2);
    }

    @Override
    public void glApplyRotateDegrees(double angleDegrees) {
        if (!isZero()) {
            // We have to use glRotatef because glRotated is missing from LWJGL
            // 2.4.2, the version Minecraft ships with. LWJGL did fix this several
            // releases ago though: http://lwjgl.org/forum/index.php?topic=4128.0
            GL11.glRotatef((float) angleDegrees, (float) x, (float) y, (float) z);
        }
    }

    @Override
    public void glApplyRotateRadians(double angleRadians) {
        if (!isZero()) {
            // see above
            GL11.glRotatef((float) (angleRadians*R2D), (float) x, (float) y, (float) z);
        }
    }

    @Override
    public void glApplyScale() {
        GL11.glScaled(x, y, z);
    }

    @Override
    public void glApplyTranslate() {
        GL11.glTranslated(x, y, z);
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
     * Set one of this vector's components, specified by the axis, to the
     * specified value.
     * @return the same vector object, modified in-place.
     */
    public Vector3 setComponent(Axis axis, double value) {
        if (axis == null) {
            throw new IllegalArgumentException("axis cannot be null");
        } else if (axis == Axis.X) {
            x = value;
        } else if (axis == Axis.Y) {
            y = value;
        } else {
            z = value;
        }
        return this;
    }

    /**
     * Swap the components of this vector: set y to the previous x value,
     * z to y, and x to z. To swap in the other direction, simply call this
     * method twice.
     * @return the same vector object, modified in-place.
     */
    public Vector3 swapComponents() {
        double tmp = z;
        z = y;
        y = x;
        x = tmp;
        return this;
    }

    /**
     * Add to this vector's x component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 addX(double xAmount) {
        x += xAmount;
        return this;
    }

    /**
     * Add to this vector's y component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 addY(double yAmount) {
        y += yAmount;
        return this;
    }

    /**
     * Add to this vector's z component.
     * @return the same vector object, modified in-place.
     */
    public Vector3 addZ(double zAmount) {
        z += zAmount;
        return this;
    }

    /**
     * Add to one of this vector's components, specified by the axis.
     * @return the same vector object, modified in-place.
     */
    public Vector3 addComponent(Axis axis, double amount) {
        return setComponent(axis, getComponent(axis) + amount);
    }

    /**
     * Add a vector to this vector.
     * @return the same vector object, modified in-place.
     */
    public Vector3 add(double xAmount, double yAmount, double zAmount) {
        x += xAmount;
        y += yAmount;
        z += zAmount;
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
     * Equivalent to <code>addX(-other.getX()).addY(-other.getY()).addZ(-other.getZ())</code>.
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
     * Equivalent to <code>set(Vector3.ZEROS)</code>.
     * @return the same vector object, modified in-place.
     */
    public Vector3 zero() {
        x = 0.0;
        y = 0.0;
        z = 0.0;
        return this;
    }

    /**
     * Set all of this vector's components to random values in [0.0, 1.0).
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
     * Set this vector to a unit vector (i.e. length=1) facing the direction
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
     * Set this vector to a unit vector (i.e. length=1) facing the direction
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
     * Multiply one of this vector's components, specified by the axis, by a
     * given factor.
     * @return the same vector object, modified in-place.
     */
    public Vector3 scaleComponent(Axis axis, double factor) {
        return setComponent(axis, getComponent(axis) * factor);
    }

    /**
     * Set each component of this vector to its negative value.
     * Equivalent to <code>scale(-1)</code>.
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
     * Ensure that one of this vector's components, specified by the axis, is
     * in the specified range.
     * @return the same vector object, modified in-place.
     */
    public Vector3 clampComponent(Axis axis, double min, double max) {
        return setComponent(axis, Math.min(max, Math.max(min, getComponent(axis))));
    }

    /**
     * Drop the fractional portion of each component by casting each to an int,
     * then back to a double. Values move closer to zero.
     * <p>E.g.: -3.25 to -3.0; -0.6 to 0.0; 0.4 to 0.0; 5.3 to 5.0.
     * @return the same vector object, modified in-place.
     */
    public Vector3 truncate() {
        x = (int) x;
        y = (int) y;
        z = (int) z;
        return this;
    }

    /**
     * Drop the fractional portion of each component using {@link Math#floor}.
     * Values move closer to negative infinity.
     * <p>E.g.: -3.25 to -4.0; -0.6 to -1.0; 0.4 to 0.0; 5.3 to 5.0. 
     * @return the same vector object, modified in-place.
     */
    public Vector3 floor() {
        x = Math.floor(x);
        y = Math.floor(y);
        z = Math.floor(z);
        return this;
    }

    /**
     * Drop the fractional portion of each component using {@link Math#ceil}.
     * Values move closer to positive infinity.
     * <p>E.g.: -3.25 to -3.0; -0.6 to 0.0; 0.4 to 1.0; 5.3 to 6.0. 
     * @return the same vector object, modified in-place.
     */
    public Vector3 ceiling() {
        x = Math.ceil(x);
        y = Math.ceil(y);
        z = Math.ceil(z);
        return this;
    }

    /**
     * Drop the fractional portion of each component using {@link Math#round}.
     * Values move to the closest whole number.
     * <p>E.g.: -3.25 to -3.0; -0.6 to -1.0; 0.4 to 0.0; 5.3 to 5.0. 
     * @return the same vector object, modified in-place.
     */
    public Vector3 round() {
        x = Math.round(x);
        y = Math.round(y);
        z = Math.round(z);
        return this;
    }

    /**
     * Set this vector to the midpoint in between it and another vector.
     * Equivalent to <code>interpolate(other, 0.5)</code>.
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

    // ========
    // Animates interface
    // ========

    @Override
    public boolean isAnimating() {
        return timeline != null && !timeline.isDone();
    }

    @Override
    public Vector3 animateStop() {
        if (timeline != null && !timeline.isDone()) {
            timeline.abort();
        }
        timeline = null;
        return this;
    }

    @Override
    public Vector3 animateStart(ReadonlyVector3 toVector, long durationMs) {
        if (toVector == null) {
            throw new IllegalArgumentException("toVector cannot be null");
        }
        newTimeline(toVector.getX(), toVector.getY(), toVector.getZ(), durationMs);
        timeline.play();
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStart(new Vector3(toX, toY, toZ), durationMs)</code>
     */
    public Vector3 animateStart(double toX, double toY, double toZ, long durationMs) {
        newTimeline(toX, toY, toZ, durationMs);
        timeline.play();
        return this;
    }

    @Override
    public Vector3 animateStartLoop(ReadonlyVector3 toVector, boolean reverse, long durationMs) {
        if (toVector == null) {
            throw new IllegalArgumentException("toVector cannot be null");
        }
        newTimeline(toVector.getX(), toVector.getY(), toVector.getZ(), durationMs);
        timeline.playLoop(reverse);
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStartLoop(new Vector3(toX, toY, toZ), reverse, durationMs)</code>
     */
    public Vector3 animateStartLoop(double toX, double toY, double toZ, boolean reverse, long durationMs) {
        newTimeline(toX, toY, toZ, durationMs);
        timeline.playLoop(reverse);
        return this;
    }

    private void newTimeline(double toX, double toY, double toZ, long durationMs) {
        animateStop();
        timeline = new Timeline(this);
        timeline.addPropertyToInterpolate("x", x, toX);
        timeline.addPropertyToInterpolate("y", y, toY);
        timeline.addPropertyToInterpolate("z", z, toZ);
        timeline.setDuration(durationMs);
    }
}
