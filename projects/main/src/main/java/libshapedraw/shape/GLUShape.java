package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Quadric;

/**
 * Intermediate base class for simple shapes rendered using GLU (OpenGL Utility
 * Library).
 * <p>
 * Supports the concept of "xray" rendering: shapes that are occluded by
 * another object can be drawn regardless, using a different style than its
 * non-occluded sections.
 */
public abstract class GLUShape extends Shape {
    public static final int DEFAULT_SLICES = 24;
    public static final int DEFAULT_STACKS = 24;
    public static final int DEFAULT_LOOPS = 6;

    private Color mainColor;
    private Color secondaryColor;
    private Quadric gluQuadric;

    public GLUShape(Vector3 origin, Color mainColor, Color secondaryColor) {
        super(origin);
        setMainColor(mainColor);
        setSecondaryColor(secondaryColor);
    }

    @Override
    protected void renderShape(MinecraftAccess mc) {
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        mainColor.glApply();
        renderGLUQuadric();
        if (secondaryColor != null) {
            GL11.glDepthFunc(GL11.GL_GREATER);
            secondaryColor.glApply();
            renderGLUQuadric();
        }
    }

    protected abstract void renderGLUQuadric();

    @Override
    public Vector3 getOrigin() {
        return super.getOrigin();
    }
    @Override
    public void setOrigin(Vector3 origin) {
        super.setOrigin(origin);
    }

    public Color getMainColor() {
        return mainColor;
    }
    public GLUShape setMainColor(Color mainColor) {
        if (mainColor == null) {
            throw new IllegalArgumentException("main color cannot be null");
        }
        this.mainColor = mainColor;
        return this;
    }

    public Color getSecondaryColor() {
        return secondaryColor;
    }
    public GLUShape setSecondaryColor(Color secondaryColor) {
        // null allowed
        this.secondaryColor = secondaryColor;
        return this;
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
}
