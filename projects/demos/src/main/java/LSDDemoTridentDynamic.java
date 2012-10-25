import java.util.HashSet;

import libshapedraw.LibShapeDraw;
import libshapedraw.animation.trident.Timeline;
import libshapedraw.animation.trident.Timeline.RepeatBehavior;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.shape.WireframeCuboid;
import libshapedraw.transform.ShapeRotate;

import org.lwjgl.input.Keyboard;

/**
 * Dynamically create animated shapes.
 * <p>
 * Pressing X on the keyboard will spawn a rotating wireframe box near the
 * player, colored randomly and rotating at a random rate. An unlimited number
 * of boxes can be created. The shapes, and their associated Timeline
 * instances, are properly cleaned up when respawning.
 * <p>
 * This illustrates an important concept: Trident Timeline instances are NOT
 * owned by the LibDrawShapes API instance. You, as the client code author, own
 * them and are responsible for cleaning up after them if their associated
 * Shape is removed.
 * <p>
 * As noted in mod_LSDDemoTridentBasic, it's relatively harmless to have a few
 * looping Timelines running at all times, if their associated Shapes exist at
 * all times anyway.
 * <p>
 * But if the Shape is going away, you don't want an active Timeline to keep
 * updating it. This wastes both CPU and memory (the Shape won't ever get
 * garbage collected as the Timeline keeps a reference to it).
 */
public class LSDDemoTridentDynamic extends BaseMod implements LSDEventListener {
    public static final String ABOUT =
            "Animate shapes dynamically using the Trident animation library.\n" +
                    "Press V to spawn a random animated shape!";

    protected LibShapeDraw libShapeDraw = new LibShapeDraw().addEventListener(this);
    private HashSet<Timeline> shapeTimelines = new HashSet<Timeline>();
    private long lastShapeSpawn; // so we can pause between each new shape spawned

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        libShapeDraw.verifyInitialized();
    }

    @Override
    public void onRespawn(LSDRespawnEvent event) {
        libShapeDraw.getShapes().clear();

        for (Timeline t : shapeTimelines) {
            // Effectively "kill -9" all Timelines.
            // t.end() and t.cancel() are the more graceful methods to stop a
            // Timeline, but since all the associated Shapes are bound for the
            // garbage collector anyway there's no need.
            // 
            // There's no thread safety issue here: lingering Timelines may be
            // updating the Shape's properties while we're working on aborting
            // them. This is harmless as the Shapes aren't going to be rendered
            // anymore.
            t.abort();
        }
        shapeTimelines.clear();
    }

    @Override
    public void onGameTick(LSDGameTickEvent event) {
        // do nothing
    }

    @Override
    public void onPreRender(LSDPreRenderEvent event) {
        if (!Keyboard.isKeyDown(Keyboard.KEY_V)) {
            return;
        }
        long now = System.currentTimeMillis();
        if (now < lastShapeSpawn + 250L) {
            return;
        }
        lastShapeSpawn = now;
        spawnAnimatedShapeAt(event.getPlayerCoords());
    }

    private void spawnAnimatedShapeAt(ReadonlyVector3 playerCoords) {
        // Create the shape 3 blocks east of the player.
        // 
        // We could (but won't, since this is a quick demo) get fancy and
        // adjust x/z based on Minecraft.thePlayer.rotationYaw so the box is
        // always right in front of the player.
        WireframeCuboid shape = new WireframeCuboid(
                playerCoords.copy().add(2.75, -1.0, -0.25),
                playerCoords.copy().add(3.25,  0.0,  0.25));
        shape.setLineStyle(new Color(Math.random(), Math.random(), Math.random(), 0.8), 3.0F, true);
        ShapeRotate rotate = new ShapeRotate(0.0, 0.1, 0.8, 0.2); // wonky axis
        shape.addTransform(rotate);
        libShapeDraw.addShape(shape);

        // Each spawned Shape get its own ShapeRotate transform and its
        // own Timeline to update it.
        // 
        // If you're animating a large number of Shapes, or if you want
        // animations to be in sync with each other, you can simply re-use a
        // single ShapeRotate (along with a single Timeline to update it) for
        // multiple Shapes.
        Timeline timeline = new Timeline(rotate);
        timeline.addPropertyToInterpolate("angle", 360.0F, 0.0F);
        timeline.setDuration((long) (3000 + Math.random()*10000));
        timeline.playLoop(RepeatBehavior.LOOP);
        shapeTimelines.add(timeline);
    }
}
