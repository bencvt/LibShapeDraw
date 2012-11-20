package libshapedraw.demos;

import libshapedraw.LibShapeDraw;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import libshapedraw.shape.WireframeCuboid;

/**
 * Demonstrates a couple basic things you can do with events:
 * 1) automatically clear all shapes owned by this mod when respawning; and
 * 2) dynamically create and update shapes, in this case based off the player's
 *    position
 * <p>
 * This mod implements the LSDEventListener interface itself, though you could
 * easily refactor this to use a different class in your mod.
 */
public class mod_LSDDemoEvents extends BaseMod implements LSDEventListener {
    public static final String ABOUT = "" +
            "Demonstrates basic events.\n" +
            "Each time you respawn, several dozen shapes spawn behind you as you move!\n" +
            "There's also a box following you...";

    protected LibShapeDraw libShapeDraw;
    private final ReadonlyVector3 BOX_RADIUS = new Vector3(0.2, 0.5, 0.2);
    private final ReadonlyVector3 FOLLOW_BOX_OFFSET = new Vector3(2.3, 1.9, -2.0);
    private WireframeCuboid followBox;
    private long lastShapeCreated;

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        // We actually don't have to keep a reference to the LibShapeDraw API
        // instance as it's also accessible in the handler methods via
        // event.getAPI().
        libShapeDraw = new LibShapeDraw().verifyInitialized().addEventListener(this).setVisibleWhenHidingGui(true);
    }

    @Override
    public void onRespawn(LSDRespawnEvent event) {
        // Remove all shapes registered by this mod.
        // If there are other mods using LibShapeDraw, this won't touch their
        // shapes. API instances are independent of each other.
        libShapeDraw.clearShapes();

        // Add a floating box that just follows the player around.
        followBox = new WireframeCuboid(0,0,0, 0,0,0); // actual coords will be set later
        followBox.setLineStyle(Color.CRIMSON.copy(), 3.0F, true);
        libShapeDraw.addShape(followBox);

        // Don't start spawning shapes until the player has been around at
        // least a couple seconds.
        lastShapeCreated = System.currentTimeMillis() + 2000;
    }

    @Override
    public void onGameTick(LSDGameTickEvent event) {
        // onGameTick is partially redundant with BaseMod.onTickInGame. Feel
        // free to leave this method empty and do your work there instead. Just
        // be aware of the difference between game ticks and render ticks:
        //  - LSDEventListener.onGameTick is always a game tick.
        //  - BaseMod.onTickInGame can be either (or neither) depending on how
        //    you registered it.

        // For the demo:
        // Every 0.5 seconds, create a shape at the player's location, up to 30
        // shapes. This will effectively make a trail of shapes behind the
        // player as they move. We could get fancy (but won't, because this is
        // a simple demo) and do stuff like:
        //  - Make the shape an arrow pointing in the direction the player is
        //    facing at the time, calculated based on
        //    Minecraft.thePlayer.rotationYaw/rotationPitch.
        //  - Add logic to only drop a shape if the player has moved far enough
        //    away from the last shape.
        long now = System.currentTimeMillis();
        if (now > lastShapeCreated + 500 && libShapeDraw.getShapes().size() - 1 < 30) {
            WireframeCuboid box = new WireframeCuboid(
                    event.getPlayerCoords().copy().subtract(BOX_RADIUS),
                    event.getPlayerCoords().copy().add(BOX_RADIUS));
            // gradually shift the color from opaque violet to transparent white
            double percent = (libShapeDraw.getShapes().size() - 1) / 30.0;
            box.setLineStyle(Color.DARK_VIOLET.copy().blend(Color.WHITE.copy().setAlpha(0.05), percent), 3.0F, false);
            libShapeDraw.addShape(box);
            lastShapeCreated = now;
        }
    }

    @Override
    public void onPreRender(LSDPreRenderEvent event) {
        // Update the corners of followBox based on the player's position.
        // We have to do this in onPreRender instead of onGameTick or the
        // shape would appear jerky.
        followBox.getLowerCorner().set(event.getPlayerCoords()).add(FOLLOW_BOX_OFFSET).subtract(BOX_RADIUS);
        followBox.getUpperCorner().set(event.getPlayerCoords()).add(FOLLOW_BOX_OFFSET).add(BOX_RADIUS);
    }
}
