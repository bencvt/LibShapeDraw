package libshapedraw.internal;

import libshapedraw.animation.trident.interpolator.PropertyInterpolator;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

/**
 * Internal class. Allows fields of type Vector3/ReadonlyVector3 to be easily
 * animated using Trident.
 * <p>
 * Note that the vector object will be reinstantiated each tick. If this is not
 * the desired behavior, animate the vector's individual components instead.
 * 
 * @see libshapedraw.animation.Animates
 */
public class ReadonlyVector3PropertyInterpolator implements PropertyInterpolator<ReadonlyVector3> {
    @Override
    public Class<ReadonlyVector3> getBasePropertyClass() {
        return ReadonlyVector3.class;
    }

    @Override
    public ReadonlyVector3 interpolate(ReadonlyVector3 from, ReadonlyVector3 to, float timelinePosition) {
        return new Vector3(
                blend(from.getX(), to.getX(), timelinePosition),
                blend(from.getY(), to.getY(), timelinePosition),
                blend(from.getZ(), to.getZ(), timelinePosition));
    }

    private static double blend(double fromValue, double toValue, float percent) {
        return fromValue + (toValue - fromValue)*percent;
    }
}
