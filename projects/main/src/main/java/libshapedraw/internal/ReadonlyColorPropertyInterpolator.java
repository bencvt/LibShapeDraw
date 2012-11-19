package libshapedraw.internal;

import libshapedraw.animation.trident.interpolator.PropertyInterpolator;
import libshapedraw.primitive.ReadonlyColor;

/**
 * Internal class. Allows fields of type Color/ReadonlyColor to be easily
 * animated using Trident.
 * <p>
 * Note that the color object will be reinstantiated each tick. If this is not
 * the desired behavior, animate the color's individual components instead.
 * 
 * @see libshapedraw.animation.Animates
 */
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
