package libshapedraw.shape;

import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;

import org.lwjgl.util.glu.Cylinder;

/**
 * Render a GLU (OpenGL Utility Library) cylinder or cone.
 */
public class GLUCylinder extends GLUShape {
    private final Cylinder cylinder;
    private float baseRadius;
    private float topRadius;
    private float height;
    private int slices;
    private int stacks;

    /**
     * @param origin the absolute world x/y/z coordinates of the sphere's center.
     * @param mainColor a Color instance specifying the RGBA values used to render the sphere
     * @param secondaryColor if non-null, the RGBA used to render occluded sections of the sphere 
     * @param baseRadius
     * @param topRadius
     * @param height
     */
    public GLUCylinder(Vector3 origin, Color mainColor, Color secondaryColor, float baseRadius, float topRadius, float height) {
        super(origin, mainColor, secondaryColor);
        cylinder = new Cylinder();
        setGLUQuadric(cylinder);
        setBaseRadius(baseRadius);
        setTopRadius(topRadius);
        setHeight(height);
        setSlices(DEFAULT_SLICES);
        setStacks(DEFAULT_STACKS);
    }

    @Override
    protected void renderGLUQuadric() {
        cylinder.draw(getBaseRadius(), getTopRadius(), getHeight(), getSlices(), getStacks());
    }

    public float getBaseRadius() {
        return baseRadius;
    }
    public GLUCylinder setBaseRadius(float baseRadius) {
        if (baseRadius <= 0.0F) {
            throw new IllegalArgumentException("expecting base radius >0.0, got " + baseRadius);
        }
        this.baseRadius = baseRadius;
        return this;
    }

    public float getTopRadius() {
        return topRadius;
    }
    public GLUCylinder setTopRadius(float topRadius) {
        if (topRadius <= 0.0F) {
            throw new IllegalArgumentException("expecting top radius >0.0, got " + topRadius);
        }
        this.topRadius = topRadius;
        return this;
    }

    public float getHeight() {
        if (height <= 0.0F) {
            throw new IllegalArgumentException("expecting height >0.0, got " + height);
        }
        return height;
    }
    public GLUCylinder setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * @return the number of subdivisions around the z axis (similar to lines
     * of longitude).
     */
    public int getSlices() {
        return slices;
    }
    public GLUCylinder setSlices(int slices) {
        if (slices < 2) {
            throw new IllegalArgumentException("expecting slices >=2, got " + slices);
        }
        this.slices = slices;
        return this;
    }

    /**
     * @return the number of subdivisions along the z axis (similar to lines
     * of latitude).
     */
    public int getStacks() {
        return stacks;
    }
    public GLUCylinder setStacks(int stacks) {
        if (stacks < 2) {
            throw new IllegalArgumentException("expecting stacks >=2, got " + stacks);
        }
        this.stacks = stacks;
        return this;
    }
}