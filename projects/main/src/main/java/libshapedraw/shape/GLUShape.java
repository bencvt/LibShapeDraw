package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Quadric;

/**
 * Intermediate base class for simple shapes rendered using GLU (OpenGL Utility
 * Library).
 */
public abstract class GLUShape extends Shape implements XrayShape {
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

    @Override
    public ReadonlyColor getMainColorReadonly() {
        return mainColor;
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

    @Override
    public ReadonlyColor getSecondaryColorReadonly() {
        return secondaryColor;
    }
    public Color getSecondaryColor() {
        return secondaryColor;
    }
    public GLUShape setSecondaryColor(Color secondaryColor) {
        // null allowed
        this.secondaryColor = secondaryColor;
        return this;
    }

    @Override
    public boolean isVisibleThroughTerrain() {
        return secondaryColor == null;
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
