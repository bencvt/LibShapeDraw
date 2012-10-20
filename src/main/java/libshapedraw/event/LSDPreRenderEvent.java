package libshapedraw.event;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.ReadonlyVector3;

/**
 * Event fired immediately before the API instance's shape collection is rendered.
 * Last-second modifications are allowed (in fact, that's the main use case for this event).
 */
public class LSDPreRenderEvent extends LSDEvent {
    private final float partialTick;
    private final boolean guiHidden;

    public LSDPreRenderEvent(LibShapeDraw apiInstance, ReadonlyVector3 playerCoords, float partialTick, boolean guiHidden) {
        super(apiInstance, playerCoords);
        this.partialTick = partialTick;
        this.guiHidden = guiHidden;
    }

    /**
     * The partial tick time: a number in [0.0, 1.0) indicating where the
     * rendering frame is in between game ticks.
     */
    public float getPartialTick() {
        return partialTick;
    }

    /**
     * True if the user has hidden the GUI (F1) and there is no active GUI screen.
     */
    public boolean isGuiHidden() {
        return guiHidden;
    }
}
