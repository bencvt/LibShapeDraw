package libshapedraw.demos;

import java.util.LinkedList;

import libshapedraw.LibShapeDraw;
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
 * Pressing V on the keyboard will spawn a rotating wireframe box near the
 * player, colored randomly and rotating at a random rate. An unlimited number
 * of boxes can be created. The shapes, and their associated looping
 * animations, are properly cleaned up when respawning.
 * <p>
 * This illustrates an important concept: animations (a.k.a. Trident Timeline
 * instances) are NOT owned by the LibDrawShapes API instance. You, as the mod
 * author, own them and are responsible for cleaning up after them if their
 * associated Shape is removed.
 * <p>
 * In other words: LibShapeDraw and Trident will not automatically stop any
 * looping animation that you start.
 * <p>
 * As noted in mod_LSDDemoTridentBasic, it's relatively harmless to have a few
 * looping animations running at all times, especially if their associated
 * Shapes exist at all times anyway.
 * <p>
 * But if the Shape is going away, you don't want an active looping animation
 * to keep updating it. This wastes CPU and is a minor memory leak: the Shape
 * won't ever get garbage collected as it's still referenced internally by the
 * animation engine.
 * <p>
 * You don't have to consider any of this for non-looping animations. Once
 * complete, animations are dereferenced by the animation engine and will be
 * eventually garbage collected without issue.
 */
public class mod_LSDDemoTridentDynamic extends BaseMod implements LSDEventListener {
    public static final String ABOUT = "" +
            "Animate shapes dynamically using the Trident animation library.\n" +
            "Press V to spawn a random animated shape!";

    protected LibShapeDraw libShapeDraw = new LibShapeDraw().addEventListener(this);
    private LinkedList<ShapeRotate> shapeRotations = new LinkedList<ShapeRotate>();
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
        libShapeDraw.clearShapes();
        for (ShapeRotate r : shapeRotations) {
            // Effectively "kill -9" all animations that we created earlier.
            // 
            // There's no thread safety issue here: lingering animations may
            // indeed be updating properties while we're working on killing
            // them off. But it's harmless as the Shapes aren't going to be
            // rendered anymore.
            r.animateStop();
        }
        shapeRotations.clear();
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

        // Start the looping animation and keep a reference to the ShapeRotate
        // so we can stop it later.
        // 
        // Each spawned Shape get its own ShapeRotate transform with its own
        // animation rate.
        // 
        // If you're animating a large number of Shapes, or if you want
        // animations to be in sync with each other, you can simply re-use a
        // single ShapeRotate for multiple Shapes. This shared instance
        // technique can also be used for Colors and other animateable objects.
        shapeRotations.add(rotate);
        rotate.animateStartLoop(360.0, false, (long) (3000 + Math.random()*10000));
    }
}
