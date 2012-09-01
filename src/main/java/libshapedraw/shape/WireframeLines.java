package libshapedraw.shape;

import java.util.Iterator;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * A series of connected line segments.
 */
public class WireframeLines extends WireframeShape {
    private Iterable<ReadonlyVector3> points;
    private int renderCap;

    public WireframeLines(Iterable<ReadonlyVector3> points) {
        setPoints(points);
        setRenderCap(-1);
    }

    /**
     * The points defining the connected line segments to render.
     * The number of line segments rendered will be at most
     * getPoints().size() - 1.
     */
    public Iterable<ReadonlyVector3> getPoints() {
        return points;
    }
    public WireframeLines setPoints(Iterable<ReadonlyVector3> points) {
        if (points == null) {
            throw new NullPointerException();
        }
        // Sanity check the type of the first element, if there is one.
        // This doesn't guarantee the ongoing type safety of the entire
        // iterable, or even of the first element... but anything we can catch
        // earlier rather than later is good.
        Iterator<ReadonlyVector3> it = points.iterator();
        if (it.hasNext() && !(it.next() instanceof ReadonlyVector3)) {
            throw new IllegalArgumentException("expecting Iterable<" + ReadonlyVector3.class.getName() + ">");
        }
        this.points = points;
        return this;
    }

    /** The maximum number of line segments to render. <0 means unlimited. */
    public int getRenderCap() {
        return renderCap;
    }
    public WireframeLines setRenderCap(int renderCap) {
        this.renderCap = renderCap;
        return this;
    }

    @Override
    public void getOrigin(Vector3 buf) {
        Iterator<ReadonlyVector3> it = getPoints().iterator();
        if (it.hasNext()) {
            buf.set(getPoints().iterator().next());
        } else {
            buf.set(Vector3.ZEROS); // won't be rendering anything anyway
        }
    }

    @Override
    protected void renderShape(MinecraftAccess mc) {
        final int renderCap = getRenderCap();
        final Iterator<ReadonlyVector3> it = getPoints().iterator();
        if (renderCap == 0 || !it.hasNext()) {
            return;
        }

        final ReadonlyColor c0 = getEffectiveLineStyle().getMainReadonlyColor();
        final ReadonlyColor c1 = getEffectiveLineStyle().getSecondaryReadonlyColor(); // can be null
        final float w0 = getEffectiveLineStyle().getMainWidth();
        final float w1 = getEffectiveLineStyle().getSecondaryWidth();

        int lineNum = 0;
        ReadonlyVector3 pointA = it.next();
        ReadonlyVector3 pointB;
        while (it.hasNext() && (renderCap < 0 || lineNum < renderCap)) {
            lineNum++;
            pointB = it.next();

            mc.startDrawing(GL11.GL_LINES);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glLineWidth(w0);
            GL11.glColor4d(c0.getRed(), c0.getGreen(), c0.getBlue(), c0.getAlpha());
            mc.addVertex(pointA);
            mc.addVertex(pointB);
            mc.finishDrawing();

            if (c1 != null) {
                mc.startDrawing(GL11.GL_LINES);
                GL11.glDepthFunc(GL11.GL_GREATER);
                GL11.glLineWidth(w1);
                GL11.glColor4d(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                mc.addVertex(pointA);
                mc.addVertex(pointB);
                mc.finishDrawing();
            }

            pointA = pointB;
        }
    }
}
