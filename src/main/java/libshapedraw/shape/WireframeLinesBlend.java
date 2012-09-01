package libshapedraw.shape;

import java.util.Collection;

import libshapedraw.primitive.ReadonlyVector3;

/**
 * A series of connected line segments that smoothly blends from one line style
 * to another along the segments.
 */
public class WireframeLinesBlend extends WireframeLinesBlendIterable {
    private Collection<ReadonlyVector3> pointsCollection;

    /**
     * Require a Collection rather than just an Iterable because we need to
     * know the size before iterating for blending.
     */
    public WireframeLinesBlend(Collection<ReadonlyVector3> points) {
        super(points);
        pointsCollection = points;
    }

    @Override
    protected int getBlendEndpoint() {
        return Math.max(getRenderCap(), getRenderCap() < 0 ? pointsCollection.size() - 1 : getRenderCap());
    }
}
