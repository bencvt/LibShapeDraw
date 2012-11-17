package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * A wireframe box, orthogonal to the x/y/z axes.
 * You can use a ShapeRotate transform to make it non-orthogonal.
 */
public class WireframeCuboid extends WireframeShape {
    private Vector3 lowerCorner;
    private Vector3 upperCorner;

    public WireframeCuboid(Vector3 lowerCorner, Vector3 upperCorner) {
        super(Vector3.ZEROS.copy());
        setRelativeToOrigin(false);
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
        getOriginReadonly();
    }
    public WireframeCuboid(double x0, double y0, double z0, double x1, double y1, double z1) {
        this(new Vector3(x0, y0, z0), new Vector3(x1, y1, z1));
    }

    public Vector3 getLowerCorner() {
        return lowerCorner;
    }
    public WireframeCuboid setLowerCorner(Vector3 lowerCorner) {
        if (lowerCorner == null) {
            throw new IllegalArgumentException("lower corner cannot be null");
        }
        this.lowerCorner = lowerCorner;
        normalize();
        return this;
    }
    public Vector3 getUpperCorner() {
        return upperCorner;
    }
    public WireframeCuboid setUpperCorner(Vector3 upperCorner) {
        if (upperCorner == null) {
            throw new IllegalArgumentException("upper corner cannot be null");
        }
        this.upperCorner = upperCorner;
        normalize();
        return this;
    }

    public void normalize() {
        if (lowerCorner.getX() <= upperCorner.getX() &&
                lowerCorner.getY() <= upperCorner.getY() &&
                lowerCorner.getZ() <= upperCorner.getZ()) {
            return;
        }
        double lo = Math.min(lowerCorner.getX(), upperCorner.getX());
        double hi = Math.max(lowerCorner.getX(), upperCorner.getX());
        lowerCorner.setX(lo);
        upperCorner.setX(hi);
        lo = Math.min(lowerCorner.getY(), upperCorner.getY());
        hi = Math.max(lowerCorner.getY(), upperCorner.getY());
        lowerCorner.setY(lo);
        upperCorner.setY(hi);
        lo = Math.min(lowerCorner.getZ(), upperCorner.getZ());
        hi = Math.max(lowerCorner.getZ(), upperCorner.getZ());
        lowerCorner.setZ(lo);
        upperCorner.setZ(hi);
    }

    @Override
    public ReadonlyVector3 getOriginReadonly() {
        normalize();
        return getOrigin().set(
                midpoint(lowerCorner.getX(), upperCorner.getX()),
                midpoint(lowerCorner.getY(), upperCorner.getY()),
                midpoint(lowerCorner.getZ(), upperCorner.getZ()));
    }
    private static double midpoint(double lo, double hi) {
        return lo + (hi - lo)/2.0;
    }

    @Override
    protected void renderLines(MinecraftAccess mc, boolean isSecondary) {
        normalize();

        // bottom
        mc.startDrawing(GL11.GL_LINE_LOOP);
        mc.addVertex(lowerCorner.getX(), lowerCorner.getY(), lowerCorner.getZ());
        mc.addVertex(lowerCorner.getX(), lowerCorner.getY(), upperCorner.getZ());
        mc.addVertex(upperCorner.getX(), lowerCorner.getY(), upperCorner.getZ());
        mc.addVertex(upperCorner.getX(), lowerCorner.getY(), lowerCorner.getZ());
        mc.finishDrawing();

        // top
        mc.startDrawing(GL11.GL_LINE_LOOP);
        mc.addVertex(lowerCorner.getX(), upperCorner.getY(), lowerCorner.getZ());
        mc.addVertex(lowerCorner.getX(), upperCorner.getY(), upperCorner.getZ());
        mc.addVertex(upperCorner.getX(), upperCorner.getY(), upperCorner.getZ());
        mc.addVertex(upperCorner.getX(), upperCorner.getY(), lowerCorner.getZ());
        mc.finishDrawing();

        // sides
        mc.startDrawing(GL11.GL_LINES);

        mc.addVertex(lowerCorner.getX(), lowerCorner.getY(), lowerCorner.getZ());
        mc.addVertex(lowerCorner.getX(), upperCorner.getY(), lowerCorner.getZ());

        mc.addVertex(lowerCorner.getX(), lowerCorner.getY(), upperCorner.getZ());
        mc.addVertex(lowerCorner.getX(), upperCorner.getY(), upperCorner.getZ());

        mc.addVertex(upperCorner.getX(), lowerCorner.getY(), lowerCorner.getZ());
        mc.addVertex(upperCorner.getX(), upperCorner.getY(), lowerCorner.getZ());

        mc.addVertex(upperCorner.getX(), lowerCorner.getY(), upperCorner.getZ());
        mc.addVertex(upperCorner.getX(), upperCorner.getY(), upperCorner.getZ());

        mc.finishDrawing();
    }
}
