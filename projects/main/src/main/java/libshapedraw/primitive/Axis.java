package libshapedraw.primitive;

/**
 * X/Y/Z axis, used by Vector3 convenience methods.
 */
public enum Axis {
    X (new Vector3(1, 0, 0)),
    Y (new Vector3(0, 1, 0)),
    Z (new Vector3(0, 0, 1));

    public final ReadonlyVector3 unitVector;

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
