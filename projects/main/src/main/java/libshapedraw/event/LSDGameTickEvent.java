package libshapedraw.event;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.ReadonlyVector3;

/**
 * Event fired once per game tick, occurring every 1/20th of a second.
 * This happens less frequently than LSDPreRenderEvent.
 * Exactly how much less depends on the end user's FPS.
 */
public class LSDGameTickEvent extends LSDEvent {
    public LSDGameTickEvent(LibShapeDraw apiInstance, ReadonlyVector3 playerCoords) {
        super(apiInstance, playerCoords);
    }
}
