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

    /**
     * A recommended alpha scaling factor to apply when making a secondary
     * color based on the main color. In other words, how much to reduce the
     * transparency of the secondary color.
     * <p>
     * This isn't obligatory. The secondary color does not necessarily have to
     * be based on the main color.
     */
    public static double SECONDARY_ALPHA = 0.25;
}
