package libshapedraw.primitive;

/**
 * X/Y/Z axis, used by Vector3 convenience methods.
 */
public enum Axis {
    X (new Vector3(1, 0, 0)),
    Y (new Vector3(0, 1, 0)),
    Z (new Vector3(0, 0, 1));

    /**
     * A vector with the axis's component equal to 1.0 and the other two 0.0.
     */
    public final ReadonlyVector3 unitVector;

    /**
     * Get the next Axis in the sequence {X, Y, Z, X, Y, Z, ...}
     */
    public Axis next() {
        if (this == X) {
            return Y;
        } else if (this == Y) {
            return Z;
        } else {
            return X;
        }
    }

    private Axis(ReadonlyVector3 unitVector) {
        this.unitVector = unitVector;
    }
}
