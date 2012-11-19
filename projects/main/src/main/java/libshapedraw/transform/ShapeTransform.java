package libshapedraw.transform;

/**
 * Control a Shape's rendering using OpenGL calls.
 */
public interface ShapeTransform {
    /**
     * Apply the transform to the current OpenGL context.
     */
    public void preRender();
}
