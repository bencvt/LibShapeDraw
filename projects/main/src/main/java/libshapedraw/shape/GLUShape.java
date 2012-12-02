package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.primitive.Vector3;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Quadric;

/**
 * Intermediate base class for simple shapes rendered using GLU (OpenGL Utility
 * Library).
 * <p>
 * The default drawing style for
 * <a href="http://lwjgl.org/javadoc/org/lwjgl/util/glu/Quadric.html">GLU quadrics</a>
 * is GLU_FILL, which will result in flat unicolor polygons. To make the shape
 * look more 3-dimensional, you can:<ul>
 * <li>Change the drawing style to use wireframe lines or points, e.g.
 *     <code>shape.getGLUQuadric().setDrawStyle(GLU.GLU_LINE)</code>; or</li>
 * <li>Apply a custom ShapeTransform to enable lighting prior to rendering the
 *     quadric.</li>
 * </ul>
 */
public abstract class GLUShape extends Shape implements XrayShape {
    public static final int DEFAULT_SLICES = 24;
    public static final int DEFAULT_STACKS = 24;
    public static final int DEFAULT_LOOPS = 6;

    private LineStyle lineStyle;
    private Quadric gluQuadric;

    public GLUShape(Vector3 origin, Color mainColor, Color secondaryColor) {
        super(origin);
        lineStyle = new LineStyle(mainColor, 1.0F, secondaryColor, 1.0F);
    }

    @Override
    protected void renderShape(MinecraftAccess mc) {
        lineStyle.glApply(false);
        renderGLUQuadric();
        if (lineStyle.glApply(true)) {
            renderGLUQuadric();
        }
    }

    protected abstract void renderGLUQuadric();

    @Override
    public Vector3 getOrigin() {
        // changed method modifier from protected to public
        return super.getOrigin();
    }
    @Override
    public void setOrigin(Vector3 origin) {
        // changed method modifier from protected to public
        super.setOrigin(origin);
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    @Override
    public ReadonlyColor getMainColorReadonly() {
        return lineStyle.getMainReadonlyColor();
    }
    /** use getLineStyle().getMainColor() */
    @Deprecated
    public Color getMainColor() {
        return lineStyle.getMainColor();
    }
    /** use getLineStyle().setMainColor() */
    @Deprecated
    public GLUShape setMainColor(Color mainColor) {
        lineStyle.setMainColor(mainColor);
        return this;
    }

    @Override
    public ReadonlyColor getSecondaryColorReadonly() {
        return lineStyle.getSecondaryReadonlyColor();
    }
    /** use getLineStyle().getSecondaryColor() */
    @Deprecated
    public Color getSecondaryColor() {
        return lineStyle.getSecondaryColor();
    }
    /** use getLineStyle().setSecondaryColor() */
    @Deprecated
    public GLUShape setSecondaryColor(Color secondaryColor) {
        lineStyle.setSecondaryColor(secondaryColor);
        return this;
    }

    @Override
    public boolean isVisibleThroughTerrain() {
        return lineStyle.hasSecondaryColor();
    }

    public Quadric getGLUQuadric() {
        return gluQuadric;
    }
    protected GLUShape setGLUQuadric(Quadric gluQuadric) {
        if (gluQuadric == null) {
            throw new IllegalArgumentException("quadric cannot be null");
        }
        this.gluQuadric = gluQuadric;
        return this;
    }

    public boolean isWireframe() {
        return getGLUQuadric().getDrawStyle() == GLU.GLU_LINE;
    }
    /**
     * Convenience method to toggle between GLU_LINE and GLU_FILL for the
     * quadric's draw style.
     * <p>
     * To specify
     * <a href="http://lwjgl.org/javadoc/org/lwjgl/util/glu/Quadric.html#setDrawStyle(int)">other draw styles</a>
     * use, e.g.: <code>getGLUQuadric().setDrawStyle(GLU.GLU_SILHOUETTE)</code>.
     */
    public GLUShape setWireframe(boolean wireframe) {
        getGLUQuadric().setDrawStyle(wireframe ? GLU.GLU_LINE : GLU.GLU_FILL);
        return this;
    }
    public GLUShape setWireframe(boolean wireframe, float lineWidth) {
        getGLUQuadric().setDrawStyle(wireframe ? GLU.GLU_LINE : GLU.GLU_FILL);
        lineStyle.setMainWidth(lineWidth);
        lineStyle.setSecondaryWidth(lineWidth);
        return this;
    }
}
