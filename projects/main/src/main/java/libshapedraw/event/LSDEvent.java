package libshapedraw.event;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.ReadonlyVector3;

/**
 * Abstract base class for all LibShapeDraw events.
 */
public abstract class LSDEvent {
    private final LibShapeDraw apiInstance;
    private final ReadonlyVector3 playerCoords;

    public LSDEvent(LibShapeDraw apiInstance, ReadonlyVector3 playerCoords) {
        this.apiInstance = apiInstance;
        this.playerCoords = playerCoords;
    }

    /**
     * The instance of the LibShapeDraw API that generated this event.
     */
    public LibShapeDraw getAPI() {
        return apiInstance;
    }

    /**
     * The player's x/y/z coordinates, corrected for partial tick movement.
     */
    public ReadonlyVector3 getPlayerCoords() {
        return playerCoords;
    }
}
