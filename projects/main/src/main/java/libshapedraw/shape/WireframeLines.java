package libshapedraw.shape;

import java.util.Iterator;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * A series of connected line segments.
 */
public class WireframeLines extends WireframeShape {
    private Iterable<ReadonlyVector3> points;
    private int renderCap;

    public WireframeLines(Vector3 origin, Iterable<ReadonlyVector3> relativePoints) {
        super(origin);
        setPoints(relativePoints);
        setRenderCap(-1);
    }
    public WireframeLines(Iterable<ReadonlyVector3> absolutePoints) {
        this(Vector3.ZEROS.copy(), absolutePoints);
        setRelativeToOrigin(false);
    }

    /**
     * The points defining the connected line segments to render.
     * Each point (except for the first and last) is the end of one line
     * segment and the start of the next. Thus the number of lines rendered
     * will be at most the number of points yielded minus one.
     */
    public Iterable<ReadonlyVector3> getPoints() {
        return points;
    }
    public WireframeLines setPoints(Iterable<ReadonlyVector3> points) {
        if (points == null) {
            throw new IllegalArgumentException("points cannot be null");
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

    /**
     * The maximum number of line segments to render. <0 means unlimited, or
     * rather limited by the number of points yielded by the iterable.
     */
    public int getRenderCap() {
        return renderCap;
    }
    public WireframeLines setRenderCap(int renderCap) {
        this.renderCap = renderCap;
        return this;
    }

    @Override
    public ReadonlyVector3 getOriginReadonly() {
        if (isRelativeToOrigin()) {
            return super.getOriginReadonly();
        }
        Iterator<ReadonlyVector3> it = getPoints().iterator();
        if (it.hasNext()) {
            return getPoints().iterator().next();
        } else {
            return null; // won't be rendering anything anyway
        }
    }

    @Override
    protected void renderLines(MinecraftAccess mc, boolean isSecondary) {
        final int renderCap = getRenderCap();
        final Iterator<ReadonlyVector3> it = getPoints().iterator();
        if (renderCap == 0 || !it.hasNext()) {
            return;
        }

        mc.startDrawing(GL11.GL_LINE_STRIP);
        if (renderCap < 0) {
            while (it.hasNext()) {
                mc.addVertex(it.next());
            }
        } else {
            int lineNum = -1; // line #1 doesn't happen until points #1 and #2 have been added
            while (it.hasNext() && lineNum < renderCap) {
                mc.addVertex(it.next());
                lineNum++;
            }
        }
        mc.finishDrawing();
    }
}
