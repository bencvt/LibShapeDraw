package libshapedraw.internal;

public class LSDInternalException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LSDInternalException(String message) {
        super(message);
    }

    public LSDInternalException(String message, Throwable t) {
        super(message, t);
    }
}
