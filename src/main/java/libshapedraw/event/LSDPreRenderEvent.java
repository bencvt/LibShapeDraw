package libshapedraw.event;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.ReadonlyVector3;

/**
 * Event fired immediately before the API instance's shape collection is rendered.
 * Last-second modifications are allowed (in fact, that's the main use case for this event).
 */
public class LSDPreRenderEvent extends LSDEvent {
    private final boolean guiHidden;

    public LSDPreRenderEvent(LibShapeDraw apiInstance, ReadonlyVector3 playerCoords, boolean guiHidden) {
        super(apiInstance, playerCoords);
        this.guiHidden = guiHidden;
    }

    /**
     * Whether the user has hidden the GUI (F1).
     */
    public boolean isGuiHidden() {
        return guiHidden;
    }
}
