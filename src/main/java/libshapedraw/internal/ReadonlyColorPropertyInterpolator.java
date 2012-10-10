package libshapedraw.internal;

import libshapedraw.animation.trident.interpolator.PropertyInterpolator;
import libshapedraw.primitive.ReadonlyColor;

public class ReadonlyColorPropertyInterpolator implements PropertyInterpolator<ReadonlyColor> {
    @Override
    public Class<ReadonlyColor> getBasePropertyClass() {
        return ReadonlyColor.class;
    }

    @Override
    public ReadonlyColor interpolate(ReadonlyColor from, ReadonlyColor to, float timelinePosition) {
        return from.copy().blend(to, timelinePosition);
    }
}
