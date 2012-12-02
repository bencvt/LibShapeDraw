package libshapedraw.primitive;

import java.io.Serializable;

/**
 * Read-only interface for Vector3 objects, allowing for compile-time safety.
 * @see Vector3
 */
public interface ReadonlyVector3 extends Serializable {
    /**
     * @return a new deep-copied mutable Vector3. Does not copy animations.
     *         <p>
     *         Same concept as Object.clone(), minus the tedious/clunky checked
     *         exception, CloneNotSupportedException.
     */
    public Vector3 copy();

    /** @return the vector's x component. */
    public double getX();

    /** @return the vector's y component. */
    public double getY();

    /** @return the vector's z component. */
    public double getZ();

    /** @return the vector's component for the given axis. */
    public double getComponent(Axis axis);

    /**
     * @return true if the specified vector component matches a value within a
     *         a margin of error (epsilon).
     * @see #EPSILON
     */
    public boolean componentEquals(Axis axis, double value, double epsilon);

    /** @return true if the vector components match the other's exactly. */
    public boolean equalsExact(ReadonlyVector3 other);

    /** @return true if the vector components match the other's exactly. */
    public boolean equalsExact(double otherX, double otherY, double otherZ);

    /**
     * @return true if the vector components match the other's within a margin
     *         of error (epsilon).
     * @see #EPSILON
     */
    public boolean equals(ReadonlyVector3 other, double epsilon);

    /**
     * @return true if the vector components match the other's within a margin
     *         of error (epsilon).
     * @see #EPSILON
     */
    public boolean equals(double otherX, double otherY, double otherZ, double epsilon);

    /**
     * A recommended margin of error to use with
     * {@link #equals(ReadonlyVector3, double)}.
     */
    public static final double EPSILON = 1E-6;

    /**
     * Equivalent to {@link #equalsExact(ReadonlyVector3)}.
     * <p>
     * This method is marked as deprecated because it's better practice to
     * explicitly state margins of error for floating-point comparisons. For a
     * non-zero margin of error, use {@link #equals(ReadonlyVector3, double)}.
     * @deprecated
     */
    @Override
    @Deprecated
    public boolean equals(Object other);

    @Override
    public int hashCode();

    @Override
    public String toString();

    /** @return true if all of this vector's components are exactly zero. */
    public boolean isZero();

    /**
     * Equivalent to <code>distanceSquared(Vector3.ZEROS)</code>.
     * @return the vector's length (aka magnitude) squared. To compare two
     *         two vectors' lengths, use this method instead of
     *         {@link #length()} as it avoids the relatively expensive sqrt
     *         call.
     */
    public double lengthSquared();

    /**
     * Equivalent to <code>distance(Vector3.ZEROS)</code>.
     * @return the vector's length (aka magnitude). This uses the relatively
     *         expensive sqrt function, so avoid calling it repeatedly.
     */
    public double length();

    /**
     * @return the distance between two points, squared. If you're trying to
     *         compare two distances, use this method instead of
     *         {@link #distance} as it avoids the relatively expensive sqrt
     *         call.
     */
    public double distanceSquared(ReadonlyVector3 other);

    /** @deprecated as of release 1.1 replaced by {@link #distanceSquared} */
    @Deprecated public double getDistanceSquared(ReadonlyVector3 other);

    /**
     * @return the distance between two points. This uses the relatively
     *         expensive sqrt function, so avoid calling it repeatedly.
     */
    public double distance(ReadonlyVector3 other);

    /** @deprecated as of release 1.1 replaced by {@link #distance} */
    @Deprecated public double getDistance(ReadonlyVector3 other);

    /** @return the dot product between two vectors: x0*x1 + y0*y1 + z0*z1. */
    public double dot(ReadonlyVector3 other);

    /**
     * @return the angle, in radians [0, pi], between two vectors. If either
     *         vector is all zeros, 0 is returned.
     */
    public double angle(ReadonlyVector3 other);

    /**
     * @return the angle, in degrees [0, 180], between two vectors. If either
     *         vector is all zeros, 0 is returned.
     */
    public double angleDegrees(ReadonlyVector3 other);

    /**
     * @return the angle, in radians (-pi/2, pi/2] of the vector direction's
     *         heading, relative to the z axis.
     */
    public double yaw();

    /**
     * @return the angle, in degrees (-180, 180] of the vector direction's
     *         heading, relative to the z axis.
     */
    public double yawDegrees();

    /**
     * @return the angle, in radians [-pi/2, pi/2] of the vector direction's
     *         elevation.
     */
    public double pitch();

    /**
     * @return the angle, in degrees [-90, 90] of the vector direction's
     *         elevation.
     */
    public double pitchDegrees();

    /**
     * @return true if the point is inside the axis-aligned bounding box
     *         specified by the two corner parameters.
     */
    public boolean isInAABB(ReadonlyVector3 lowerCorner, ReadonlyVector3 upperColor);

    /** @return true if the point is inside the sphere. */
    public boolean isInSphere(ReadonlyVector3 origin, double radius);

    /**
     * Convenience method to update the OpenGL state with this vector.
     * <p>
     * Equivalent to
     * <code>GL11.glRotated(angleDegrees, v.getX(), v.getY(), v.getZ())</code>.
     * <p>
     * Calling this method on a zero vector is a no-op.
     * <p>
     * "Un-applying" the rotation (e.g., glPushMatrix and glPopMatrix)
     * is the responsibility of the caller.
     * <p>
     * All of these operations are actually
     * <a href="http://www.opengl.org/wiki/Legacy_OpenGL">deprecated in OpenGL
     * 3.0</a>, but Minecraft is just old school like that.
     */
    public void glApplyRotateDegrees(double angleDegrees);

    /**
     * Convenience method to update the OpenGL state with this vector.
     * <p>
     * Equivalent to
     * <code>GL11.glRotated(angleRadians*180.0/Math.PI, v.getX(), v.getY(), v.getZ())</code>.
     * <p>
     * Calling this method on a zero vector is a no-op.
     * <p>
     * "Un-applying" the rotation (e.g., glPushMatrix and glPopMatrix)
     * is the responsibility of the caller.
     * <p>
     * All of these operations are actually
     * <a href="http://www.opengl.org/wiki/Legacy_OpenGL">deprecated in OpenGL
     * 3.0</a>, but Minecraft is just old school like that.
     */
    public void glApplyRotateRadians(double angleRadians);

    /**
     * Convenience method to update the OpenGL state with this vector.
     * <p>
     * Equivalent to
     * <code>GL11.glScaled(v.getX(), v.getY(), v.getZ())</code>.
     * <p>
     * "Un-applying" the scaling (e.g., glPushMatrix and glPopMatrix)
     * is the responsibility of the caller.
     * <p>
     * All of these operations are actually
     * <a href="http://www.opengl.org/wiki/Legacy_OpenGL">deprecated in OpenGL
     * 3.0</a>, but Minecraft is just old school like that.
     */
    public void glApplyScale();

    /**
     * Convenience method to update the OpenGL state with this vector.
     * <p>
     * Equivalent to
     * <code>GL11.glTranslated(v.getX(), v.getY(), v.getZ())</code>.
     * <p>
     * "Un-applying" the translation (e.g., glPushMatrix and glPopMatrix)
     * is the responsibility of the caller.
     * <p>
     * All of these operations are actually
     * <a href="http://www.opengl.org/wiki/Legacy_OpenGL">deprecated in OpenGL
     * 3.0</a>, but Minecraft is just old school like that.
     */
    public void glApplyTranslate();

    /** @return true if this vector is being updated by an active animation. */
    public boolean isAnimating();
}
