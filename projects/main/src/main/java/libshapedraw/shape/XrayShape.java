package libshapedraw.shape;

import libshapedraw.primitive.ReadonlyColor;

/**
 * A Shape that supports "xray" rendering: sections of the shape that are
 * occluded by another object can be drawn regardless, using a different color
 * (often the same color with reduced transparency) than non-occluded sections.
 */
public interface XrayShape {
    public ReadonlyColor getMainColorReadonly();

    public ReadonlyColor getSecondaryColorReadonly();

    /** @return true if getSecondaryColorReadonly() is non-null. */
    public boolean isVisibleThroughTerrain();
}
