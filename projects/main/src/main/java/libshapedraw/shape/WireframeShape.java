package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.primitive.ReadonlyLineStyle;
import libshapedraw.primitive.Vector3;

/**
 * Intermediate base class for shapes that are rendered using lines.
 */
public abstract class WireframeShape extends Shape implements XrayShape {
    private LineStyle lineStyle;

    public WireframeShape(Vector3 origin) {
        super(origin);
    }

    @Override
    protected void renderShape(MinecraftAccess mc) {
        ReadonlyLineStyle drawStyle = getEffectiveLineStyle();
        drawStyle.glApply(false);
        renderLines(mc, false);
        if (drawStyle.glApply(true)) {
            renderLines(mc, true);
        }
    }

    /**
     * Using the MinecraftAccess param, interact with the Tessellator to draw vertices.
     * The line depth function/width/color has already been set up.
     */
    protected void renderLines(MinecraftAccess mc, boolean isSecondary) {
        // do nothing; it's up to the derived class to override either this
        // method or renderShape.
    }

    public LineStyle getLineStyle() {
        return lineStyle;
    }

    protected ReadonlyLineStyle getEffectiveLineStyle() {
        return lineStyle == null ? LineStyle.DEFAULT : lineStyle;
    }

    public WireframeShape setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
        return this;
    }

    /**
     * Convenience method.
     * @see LineStyle#set
     */
    public WireframeShape setLineStyle(Color color, float width, boolean visibleThroughTerrain) {
        if (lineStyle == null) {
            lineStyle = new LineStyle(color, width, visibleThroughTerrain);
        } else {
            lineStyle.set(color, width, visibleThroughTerrain);
        }
        return this;
    }

    @Override
    public ReadonlyColor getMainColorReadonly() {
        return getEffectiveLineStyle().getMainReadonlyColor();
    }

    @Override
    public ReadonlyColor getSecondaryColorReadonly() {
        return getEffectiveLineStyle().getSecondaryReadonlyColor();
    }

    @Override
    public boolean isVisibleThroughTerrain() {
        return getEffectiveLineStyle().hasSecondaryColor();
    }
}
