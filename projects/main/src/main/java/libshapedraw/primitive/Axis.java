package libshapedraw.primitive;

/**
 * X/Y/Z axis, used by Vector3 convenience methods.
 */
public enum Axis {
    X, Y, Z;

    public Axis next() {
        if (this == X) {
            return Y;
        } else if (this == Y) {
            return Z;
        } else {
            return X;
        }
    }
}
