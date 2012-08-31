package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * A single line segment, going from point A to point B.
 */
public class WireframeLine extends WireframeShape {
    private Vector3 pointA;
    private Vector3 pointB;

    public WireframeLine(Vector3 a, Vector3 b) {
        setPointA(a);
        setPointB(b);
    }
    public WireframeLine(double ax, double ay, double az, double bx, double by, double bz) {
        this(new Vector3(ax, ay, az), new Vector3(bx, by, bz));
    }

    public Vector3 getPointA() {
        return pointA;
    }
    public WireframeLine setPointA(Vector3 a) {
        if (a == null) {
            throw new NullPointerException();
        }
        pointA = a;
        return this;
    }
    public Vector3 getPointB() {
        return pointB;
    }
    public WireframeLine setPointB(Vector3 b) {
        if (b == null) {
            throw new NullPointerException();
        }
        pointB = b;
        return this;
    }

    @Override
    public void getOrigin(Vector3 buf) {
        buf.set(pointA);
    }

    @Override
    protected void renderLines(MinecraftAccess mc) {
        mc.startDrawing(GL11.GL_LINES);
        mc.addVertex(pointA);
        mc.addVertex(pointB);
        mc.finishDrawing();
    }
}
