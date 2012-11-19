package libshapedraw.transform;

/**
 * Control a Shape's rendering using OpenGL calls.
 */
public interface ShapeTransform {
    /**
     * Apply the transform to the current OpenGL context.
     * <p>
     * XXX: This method is pseudo-deprecated: it will be renamed to glApply
     *      in LibShapeDraw 2.0.
     */
    public void preRender();
}
