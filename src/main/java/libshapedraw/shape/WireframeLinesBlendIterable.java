package libshapedraw.shape;

import java.util.Iterator;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.primitive.ReadonlyVector3;

import org.lwjgl.opengl.GL11;

/**
 * A series of connected line segments that smoothly blends from one line style
 * to another along the segments.
 * <p>
 * For most use cases you'll want to use WireframeLinesBlend instead, which
 * takes a Collection of ReadonlyVector3s rather than an Iterable. Only use
 * this class if you want to blend based solely on the render cap, not on the
 * number of points.
 */
public class WireframeLinesBlendIterable extends WireframeLines {
    private LineStyle blendToLineStyle;

    public WireframeLinesBlendIterable(Iterable<ReadonlyVector3> points) {
        super(points);
    }

    public LineStyle getBlendToLineStyle() {
        return blendToLineStyle;
    }
    public WireframeLinesBlendIterable setBlendToLineStyle(LineStyle blendToLineStyle) {
        this.blendToLineStyle = blendToLineStyle;
        return this;
    }
    /**
     * Convenience method.
     * @see LineStyle.set
     */
    public WireframeLinesBlendIterable setBlendToLineStyle(Color color, float width, boolean visibleThroughTerrain) {
        if (blendToLineStyle == null) {
            blendToLineStyle = new LineStyle(color, width, visibleThroughTerrain);
        } else {
            blendToLineStyle.set(color, width, visibleThroughTerrain);
        }
        return this;
    }

    protected int getBlendEndpoint() {
        return getRenderCap();
    }

    @Override
    protected void renderShape(MinecraftAccess mc) {
        if (blendToLineStyle == null || getBlendEndpoint() < 0) {
            super.renderShape(mc);
            return;
        }

        final int renderCap = getRenderCap();
        final Iterator<ReadonlyVector3> it = getPoints().iterator();
        if (getRenderCap() == 0 || !it.hasNext()) {
            return;
        }

        final ReadonlyColor c0 = getEffectiveLineStyle().getMainReadonlyColor();
        final ReadonlyColor c1 = getEffectiveLineStyle().getSecondaryReadonlyColor(); // can be null
        final float w0 = getEffectiveLineStyle().getMainWidth();
        final float w1 = getEffectiveLineStyle().getSecondaryWidth();

        final ReadonlyColor bc0 = blendToLineStyle.getMainColor();
        final ReadonlyColor bc1 = blendToLineStyle.getSecondaryColor(); // can be null
        final float bw0 = blendToLineStyle.getMainWidth();
        final float bw1 = blendToLineStyle.getSecondaryWidth();

        final float blendEndpoint = getBlendEndpoint();

        int lineNum = 0;
        ReadonlyVector3 pointA = it.next();
        ReadonlyVector3 pointB;
        while (it.hasNext() && (renderCap < 0 || lineNum < renderCap)) {
            lineNum++;
            pointB = it.next();
            float percent = lineNum / blendEndpoint;

            mc.startDrawing(GL11.GL_LINES);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
            GL11.glLineWidth(blend(w0, bw0, percent));
            // c0.copy().blend(bc0, percent) would work, but this is a
            // rendering method. Creating thousands of temporary objects that
            // will just get GC'd should be avoided.
            GL11.glColor4d(
                    blend(c0.getRed(),   bc0.getRed(),   percent),
                    blend(c0.getGreen(), bc0.getGreen(), percent),
                    blend(c0.getBlue(),  bc0.getBlue(),  percent),
                    blend(c0.getAlpha(), bc0.getAlpha(), percent));
            mc.addVertex(pointA);
            mc.addVertex(pointB);
            mc.finishDrawing();

            if (c1 != null) {
                mc.startDrawing(GL11.GL_LINES);
                GL11.glDepthFunc(GL11.GL_GREATER);
                if (blendToLineStyle.hasSecondaryColor()) {
                    GL11.glLineWidth(blend(w1, bw1, percent));
                    // c1.copy().blend(bc1, percent)
                    GL11.glColor4d(
                            blend(c1.getRed(),   bc1.getRed(),   percent),
                            blend(c1.getGreen(), bc1.getGreen(), percent),
                            blend(c1.getBlue(),  bc1.getBlue(),  percent),
                            blend(c1.getAlpha(), bc1.getAlpha(), percent));
                } else {
                    // secondary style is static
                    GL11.glLineWidth(w1);
                    GL11.glColor4d(c1.getRed(), c1.getGreen(), c1.getBlue(), c1.getAlpha());
                }
                mc.addVertex(pointA);
                mc.addVertex(pointB);
                mc.finishDrawing();
            }

            pointA = pointB;
        }
    }

    private static float blend(float fromValue, float toValue, float percent) {
        return fromValue + (toValue - fromValue)*percent;
    }
    private static double blend(double fromValue, double toValue, double percent) {
        return fromValue + (toValue - fromValue)*percent;
    }
}
