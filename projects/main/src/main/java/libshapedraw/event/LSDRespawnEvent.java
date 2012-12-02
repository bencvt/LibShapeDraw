package libshapedraw.event;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.ReadonlyVector3;

/**
 * Event fired when the player respawns into a new world instance. This does
 * not necessarily mean the world changed; the player could be respawning after
 * death or teleportation.
 * <p>
 * Provided as a convenience; re-initializing the canvas of shapes after the
 * player enters a new server is a common use case.
 * <p>
 * Caveat: do not rely on playerCoords to be accurate. Minecraft will often
 * reposition the player shortly after respawning.
 */
public class LSDRespawnEvent extends LSDEvent {
    private final boolean newServer;
    private final boolean newDimension;

    public LSDRespawnEvent(LibShapeDraw apiInstance, ReadonlyVector3 playerCoords, boolean newServer, boolean newDimension) {
        super(apiInstance, playerCoords);
        this.newServer = newServer;
        this.newDimension = newDimension;
    }

    /**
     * Whether the player is spawning for the first time during a server
     * connection session. This includes single player worlds, which use an
     * integrated server.
     * <p>
     * Reconnecting to the same dedicated server will result in another event
     * with isNewServer==true.
     * <p>
     * Implies isNewDimension.
     */
    public boolean isNewServer() {
        return newServer;
    }

    /**
     * Whether the player respawned in a different dimension (e.g., taking a Nether/End portal,
     * or dying in one of those dimensions then respawning back in the overworld.)
     * <p>
     * If true, the world definitely changed.
     * If false, it's not safe to assume either way. The server might be running a multi-world plugin.
     * <p>
     * Always true if isNewServer.
     */
    public boolean isNewDimension() {
        return newDimension;
    }
}
