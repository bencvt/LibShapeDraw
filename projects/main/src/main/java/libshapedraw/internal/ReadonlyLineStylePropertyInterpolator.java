package libshapedraw.internal;

import libshapedraw.animation.trident.interpolator.PropertyInterpolator;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyLineStyle;

/**
 * Internal class. Allows fields of type LineStyle/ReadonlyLineStyle to be
 * easily animated using Trident.
 * <p>
 * Note that the style object will be reinstantiated each tick. If this is not
 * the desired behavior, animate the style's individual components instead.
 * 
 * @see libshapedraw.animation.Animates
 */
public class ReadonlyLineStylePropertyInterpolator implements PropertyInterpolator<ReadonlyLineStyle> {
    @Override
    public Class<ReadonlyLineStyle> getBasePropertyClass() {
        return ReadonlyLineStyle.class;
    }

    @Override
    public ReadonlyLineStyle interpolate(ReadonlyLineStyle from, ReadonlyLineStyle to, float timelinePosition) {
        LineStyle result = from.copy();
        result.getMainColor().blend(to.getMainReadonlyColor(), timelinePosition);
        if (result.hasSecondaryColor()) {
            result.getSecondaryColor().blend(to.getSecondaryReadonlyColor(), timelinePosition);
        }
        result.setMainWidth(blend(from.getMainWidth(), to.getMainWidth(), timelinePosition));
        result.setSecondaryWidth(blend(from.getSecondaryWidth(), to.getSecondaryWidth(), timelinePosition));
        return result;
    }

    private static float blend(float fromValue, float toValue, float percent) {
        return fromValue + (toValue - fromValue)*percent;
    }
}
