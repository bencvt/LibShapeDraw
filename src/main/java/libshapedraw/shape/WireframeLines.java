package libshapedraw.shape;

import java.util.Collection;
import java.util.Iterator;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

/**
 * A series of connected line segments.
 */
public class WireframeLines extends WireframeShape {
    private Collection<Vector3> points;
    private int renderCap;

    public WireframeLines(Collection<Vector3> points) {
        setPoints(points).setRenderCap(-1);
    }

    /**
     * The points defining the connected line segments to render.
     * The number of line segments rendered will be at most
     * getPoints().size() - 1.
     */
    public Collection<Vector3> getPoints() {
        return points;
    }
    public WireframeLines setPoints(Collection<Vector3> points) {
        if (points == null) {
            throw new NullPointerException();
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
        Iterator<Vector3> it = getPoints().iterator();
        if (it.hasNext()) {
            buf.set(getPoints().iterator().next());
        } else {
            buf.set(Vector3.ZEROS); // won't be rendering anything anyway
        }
    }

    @Override
    protected void renderShape(MinecraftAccess mc) {
        if (getRenderCap() == 0 || getPoints().size() < 2) {
            return;
        }

        final ReadonlyColor c0 = getEffectiveLineStyle().getMainReadonlyColor();
        final ReadonlyColor c1 = getEffectiveLineStyle().getSecondaryReadonlyColor(); // can be null
        final float w0 = getEffectiveLineStyle().getMainWidth();
        final float w1 = getEffectiveLineStyle().getSecondaryWidth();

        final int effectiveRenderCap = getRenderCap() < 0 ? getPoints().size()-1 : getRenderCap();
        final Iterator<Vector3> it = getPoints().iterator();
        int i = 0;
        Vector3 prevPoint = it.next();
        Vector3 curPoint;
        while (i < effectiveRenderCap && it.hasNext()) {
            i++;
            curPoint = it.next();
            mc.startDrawing(GL11.GL_LINES);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glLineWidth(w0);
            GL11.glColor4d(c0.getRed(), c0.getGreen(), c0.getBlue(), c0.getAlpha());
            mc.addVertex(prevPoint);
            mc.addVertex(curPoint);
            mc.finishDrawing();
            if (c1 != null) {
                mc.startDrawing(GL11.GL_LINES);
                GL11.glDepthFunc(GL11.GL_GREATER);
                GL11.glLineWidth(w1);
                GL11.glColor4d(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                mc.addVertex(prevPoint);
                mc.addVertex(curPoint);
                mc.finishDrawing();
            }
            prevPoint = curPoint;
        }
    }
}
