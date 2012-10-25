package libshapedraw.shape;

import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;

import org.lwjgl.util.glu.Disk;

/**
 * Render a GLU (OpenGL Utility Library) disk.
 */
public class GLUDisk extends GLUShape {
    private final Disk disk;
    private float innerRadius;
    private float outerRadius;
    private int slices;
    private int loops;

    /**
     * @param origin the absolute world x/y/z coordinates of the sphere's center.
     * @param mainColor a Color instance specifying the RGBA values used to render the sphere
     * @param secondaryColor if non-null, the RGBA used to render occluded sections of the sphere 
     * @param innerRadius
     * @param outerRadius
     */
    public GLUDisk(Vector3 origin, Color mainColor, Color secondaryColor, float innerRadius, float outerRadius) {
        super(origin, mainColor, secondaryColor);
        disk = new Disk();
        setGLUQuadric(disk);
        setInnerRadius(innerRadius);
        setOuterRadius(outerRadius);
        setSlices(DEFAULT_SLICES);
        setLoops(DEFAULT_LOOPS);
    }

    @Override
    protected void renderGLUQuadric() {
        disk.draw(getInnerRadius(), getOuterRadius(), getSlices(), getLoops());
    }

    public float getInnerRadius() {
        return innerRadius;
    }
    public GLUDisk setInnerRadius(float innerRadius) {
        if (innerRadius < 0.0F) {
            throw new IllegalArgumentException("expecting inner radius >=0.0, got " + innerRadius);
        }
        this.innerRadius = innerRadius;
        return this;
    }

    public float getOuterRadius() {
        return outerRadius;
    }
    public GLUDisk setOuterRadius(float outerRadius) {
        if (outerRadius <= 0.0F) {
            throw new IllegalArgumentException("expecting outer radius >0.0, got " + outerRadius);
        }
        this.outerRadius = outerRadius;
        return this;
    }

    /**
     * @return the number of subdivisions around the z axis (similar to lines
     * of longitude).
     */
    public int getSlices() {
        return slices;
    }
    public GLUDisk setSlices(int slices) {
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
    public GLUDisk setLoops(int loops) {
        if (loops < 2) {
            throw new IllegalArgumentException("expecting loops >=2, got " + loops);
        }
        this.loops = loops;
        return this;
    }
}
