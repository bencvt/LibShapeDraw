package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class WireframeLineSegment extends WireframeShape {
    private Vector3 pointA;
    private Vector3 pointB;

    public WireframeLineSegment(Vector3 a, Vector3 b) {
        this.setPointA(a);
        this.setPointB(b);
    }
    public WireframeLineSegment(double ax, double ay, double az, double bx, double by, double bz) {
        this(new Vector3(ax, ay, az), new Vector3(bx, by, bz));
    }

    public Vector3 getPointA() {
        return pointA;
    }
    public WireframeLineSegment setPointA(Vector3 a) {
        pointA = a;
        return this;
    }
    public Vector3 getPointB() {
        return pointB;
    }
    public WireframeLineSegment setPointB(Vector3 b) {
        pointB = b;
        return this;
    }

    @Override
    public void renderLines(MinecraftAccess mc) {
        mc.startDrawing(GL11.GL_LINES);
        mc.addVertex(pointA);
        mc.addVertex(pointB);
        mc.finishDrawing();
    }
}
