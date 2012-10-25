package libshapedraw.internal;

import libshapedraw.animation.trident.interpolator.PropertyInterpolator;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyLineStyle;

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
