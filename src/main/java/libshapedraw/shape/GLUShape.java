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
        Color color = getMainColor();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        renderGLUQuadric();

        if (getSecondaryColor() != null) {
            color = getSecondaryColor();
            GL11.glDepthFunc(GL11.GL_GREATER);
            GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
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
            throw new NullPointerException();
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
            throw new NullPointerException();
        }
        this.gluQuadric = gluQuadric;
        return this;
    }
}
