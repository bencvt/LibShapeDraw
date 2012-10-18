package libshapedraw.internal;

public class LSDInternalReflectionException extends LSDInternalException {
    private static final long serialVersionUID = 1L;

    public LSDInternalReflectionException(String message) {
        super(message);
    }

    public LSDInternalReflectionException(String message, Exception e) {
        super(message, e);
    }
}
