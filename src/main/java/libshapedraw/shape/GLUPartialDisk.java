package libshapedraw.shape;

import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;

import org.lwjgl.util.glu.PartialDisk;

/**
 * Render a GLU (OpenGL Utility Library) partial disk.
 */
public class GLUPartialDisk extends GLUShape {
    private final PartialDisk partialDisk;
    private float innerRadius;
    private float outerRadius;
    private float startAngle;
    private float sweepAngle;
    private int slices;
    private int loops;

    /**
     * @param origin the absolute world x/y/z coordinates of the sphere's center.
     * @param mainColor a Color instance specifying the RGBA values used to render the sphere
     * @param secondaryColor if non-null, the RGBA used to render occluded sections of the sphere 
     * @param innerRadius
     * @param outerRadius
     */
    public GLUPartialDisk(Vector3 origin, Color mainColor, Color secondaryColor, float innerRadius, float outerRadius, float startAngle, float sweepAngle) {
        super(origin, mainColor, secondaryColor);
        partialDisk = new PartialDisk();
        setGLUQuadric(partialDisk);
        setInnerRadius(innerRadius);
        setOuterRadius(outerRadius);
        setStartAngle(startAngle);
        setSweepAngle(sweepAngle);
        setSlices(DEFAULT_SLICES);
        setLoops(DEFAULT_LOOPS);
    }

    @Override
    protected void renderGLUQuadric() {
        partialDisk.draw(getInnerRadius(), getOuterRadius(), getSlices(), getLoops(), 0,0);//XXX
    }

    public float getInnerRadius() {
        return innerRadius;
    }
    public GLUPartialDisk setInnerRadius(float innerRadius) {
        if (innerRadius < 0.0F) {
            throw new IllegalArgumentException("expecting inner radius >=0.0, got " + innerRadius);
        }
        this.innerRadius = innerRadius;
        return this;
    }

    public float getOuterRadius() {
        return outerRadius;
    }
    public GLUPartialDisk setOuterRadius(float outerRadius) {
        if (outerRadius <= 0.0F) {
            throw new IllegalArgumentException("expecting outer radius >0.0, got " + outerRadius);
        }
        this.outerRadius = outerRadius;
        return this;
    }

    public float getStartAngle() {
        return startAngle;
    }
    public GLUPartialDisk setStartAngle(float startAngle) {
        this.startAngle = startAngle;
        return this;
    }

    public float getSweepAngle() {
        return sweepAngle;
    }
    public GLUPartialDisk setSweepAngle(float sweepAngle) {
        this.sweepAngle = sweepAngle;
        return this;
    }

    /**
     * @return the number of subdivisions around the z axis (similar to lines
     * of longitude).
     */
    public int getSlices() {
        return slices;
    }
    public GLUPartialDisk setSlices(int slices) {
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
    public int getLoops() {
        return loops;
    }
    public GLUPartialDisk setLoops(int loops) {
        if (loops < 2) {
            throw new IllegalArgumentException("expecting loops >=2, got " + loops);
        }
        this.loops = loops;
        return this;
    }
}
