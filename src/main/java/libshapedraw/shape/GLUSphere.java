package libshapedraw.shape;

import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;

import org.lwjgl.util.glu.Sphere;

/**
 * Render a GLU (OpenGL Utility Library) sphere.
 */
public class GLUSphere extends GLUShape {
    private final Sphere sphere;
    private float radius;
    private int slices;
    private int stacks;

    /**
     * @param origin the absolute world x/y/z coordinates of the sphere's center.
     * @param mainColor a Color instance specifying the RGBA values used to render the sphere
     * @param secondaryColor if non-null, the RGBA used to render occluded sections of the sphere 
     * @param radius
     */
    public GLUSphere(Vector3 origin, Color mainColor, Color secondaryColor, float radius) {
        super(origin, mainColor, secondaryColor);
        sphere = new Sphere();
        setGLUQuadric(sphere);
        setRadius(radius);
        setSlices(DEFAULT_SLICES);
        setStacks(DEFAULT_STACKS);
    }

    @Override
    protected void renderGLUQuadric() {
        sphere.draw(getRadius(), getSlices(), getStacks());
    }

    public float getRadius() {
        return radius;
    }
    public GLUSphere setRadius(float radius) {
        if (radius <= 0.0F) {
            throw new IllegalArgumentException("expecting radius >0.0, got " + radius);
        }
        this.radius = radius;
        return this;
    }

    /**
     * @return the number of subdivisions around the z axis (similar to lines
     * of longitude).
     */
    public int getSlices() {
        return slices;
    }
    public GLUSphere setSlices(int slices) {
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
    public GLUSphere setStacks(int stacks) {
        if (stacks < 2) {
            throw new IllegalArgumentException("expecting stacks >=2, got " + stacks);
        }
        this.stacks = stacks;
        return this;
    }
}
