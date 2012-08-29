package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyLineStyle;

import org.lwjgl.opengl.GL11;

/**
 * Intermediate base class for shapes that are rendered using lines.
 * <p>
 * Supports the concept of "xray" lines: shapes that are occluded by another object
 * can be drawn regardless, using a different style than its non-occluded sections.
 */
public abstract class WireframeShape extends Shape {
    private LineStyle lineStyle;

    @Override
    public final void renderShape(MinecraftAccess mc) {
        ReadonlyLineStyle drawStyle = getEffectiveLineStyle();
        Color color = drawStyle.getMainColor();
        float width = drawStyle.getMainWidth();
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
        GL11.glLineWidth(width);
        renderLines(mc);
        if (drawStyle.hasSecondaryColor()) {
            color = drawStyle.getSecondaryColor();
            width = drawStyle.getSecondaryWidth();
            GL11.glDepthFunc(GL11.GL_GREATER);
            GL11.glColor4d(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
            GL11.glLineWidth(width);
            renderLines(mc);
        }
    }

    public abstract void renderLines(MinecraftAccess mc);

    public boolean isVisibleThroughTerrain() {
        return getEffectiveLineStyle().hasSecondaryColor();
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public ReadonlyLineStyle getEffectiveLineStyle() {
        return lineStyle == null ? LineStyle.DEFAULT : lineStyle;
    }

    public WireframeShape setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
        return this;
    }

    /**
     * Convenience method.
     * @see LineStyle.set
     */
    public WireframeShape setLineStyle(Color color, float width, boolean visibleThroughTerrain) {
        if (lineStyle == null) {
            lineStyle = new LineStyle(color, width, visibleThroughTerrain);
        } else {
            lineStyle.set(color, width, visibleThroughTerrain);
        }
        return this;
    }
}
