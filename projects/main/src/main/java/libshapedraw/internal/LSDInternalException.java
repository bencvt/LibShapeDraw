package libshapedraw.internal;

public class LSDInternalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LSDInternalException(String message) {
        super(message);
    }

    public LSDInternalException(String message, Exception e) {
        super(message, e);
    }
}
