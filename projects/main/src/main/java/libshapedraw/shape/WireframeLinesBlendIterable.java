package libshapedraw.shape;

import java.util.Iterator;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.primitive.ReadonlyLineStyle;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

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

    public WireframeLinesBlendIterable(Vector3 origin, Iterable<ReadonlyVector3> relativePoints) {
        super(origin, relativePoints);
    }
    public WireframeLinesBlendIterable(Iterable<ReadonlyVector3> absolutePoints) {
        super(absolutePoints);
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
     * @see LineStyle#set
     */
    public WireframeLinesBlendIterable setBlendToLineStyle(Color color, float width, boolean visibleThroughTerrain) {
        if (blendToLineStyle == null) {
            blendToLineStyle = new LineStyle(color, width, visibleThroughTerrain);
        } else {
            blendToLineStyle.set(color, width, visibleThroughTerrain);
        }
        return this;
    }

    /**
     * The "blend endpoint" refers to the last line to be rendered, which
     * should be 100% getBlendToLineStyle().
     */
    protected int getBlendEndpoint() {
        return getRenderCap() - 1;
    }

    @Override
    protected void renderLines(MinecraftAccess mc, boolean isSecondary) {
        final ReadonlyLineStyle fromStyle = getEffectiveLineStyle();
        final ReadonlyLineStyle toStyle = getBlendToLineStyle();
        if (toStyle == null || getBlendEndpoint() <= 0) {
            super.renderLines(mc, isSecondary);
            return;
        }
        if (isSecondary && !toStyle.hasSecondaryColor()) {
            super.renderLines(mc, isSecondary);
            return;
        }

        final int renderCap = getRenderCap();
        final Iterator<ReadonlyVector3> it = getPoints().iterator();
        if (renderCap == 0 || !it.hasNext()) {
            return;
        }

        final ReadonlyColor fromColor;
        final ReadonlyColor toColor;
        final float fromWidth;
        final float toWidth;
        // we've already checked for all null cases earlier
        if (isSecondary) {
            fromColor = fromStyle.getSecondaryReadonlyColor();
            toColor   =   toStyle.getSecondaryReadonlyColor();
            fromWidth = fromStyle.getSecondaryWidth();
            toWidth   =   toStyle.getSecondaryWidth();
        } else {
            fromColor = fromStyle.getMainReadonlyColor();
            toColor   =   toStyle.getMainReadonlyColor();
            fromWidth = fromStyle.getMainWidth();
            toWidth   =   toStyle.getMainWidth();
        }
        final float blendEndpoint = getBlendEndpoint();

        // We can't use GL_LINE_STRIP in a single drawing session because each
        // line segment has its own style.
        ReadonlyVector3 pointA = it.next();
        ReadonlyVector3 pointB;
        int lineNum = 0;
        while (it.hasNext() && (renderCap < 0 || lineNum < renderCap)) {
            pointB = it.next();
            float percent = lineNum / blendEndpoint;

            mc.startDrawing(GL11.GL_LINES);
            GL11.glLineWidth(blend(fromWidth, toWidth, percent));
            // fromColor.copy().blend(toColor, percent) would work, but this is
            // a rendering method. Creating thousands of temporary objects that
            // will just get GC'd should be avoided, so we operate on the
            // individual RGBA components.
            GL11.glColor4d(
                    blend(fromColor.getRed(),   toColor.getRed(),   percent),
                    blend(fromColor.getGreen(), toColor.getGreen(), percent),
                    blend(fromColor.getBlue(),  toColor.getBlue(),  percent),
                    blend(fromColor.getAlpha(), toColor.getAlpha(), percent));
            mc.addVertex(pointA);
            mc.addVertex(pointB);
            mc.finishDrawing();

            pointA = pointB;
            lineNum++;
        }
    }

    private static float blend(float fromValue, float toValue, float percent) {
        return fromValue + (toValue - fromValue)*percent;
    }
    private static double blend(double fromValue, double toValue, double percent) {
        return fromValue + (toValue - fromValue)*percent;
    }
}
