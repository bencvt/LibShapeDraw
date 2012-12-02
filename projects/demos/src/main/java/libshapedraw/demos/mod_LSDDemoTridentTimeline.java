package libshapedraw.demos;

import libshapedraw.LibShapeDraw;
import libshapedraw.animation.trident.Timeline;
import libshapedraw.animation.trident.Timeline.RepeatBehavior;
import libshapedraw.animation.trident.ease.Sine;
import libshapedraw.primitive.Axis;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;
import libshapedraw.shape.GLUCylinder;
import libshapedraw.shape.XrayShape;
import libshapedraw.transform.ShapeRotate;
import libshapedraw.transform.ShapeTranslate;

/**
 * The built-in animateStart/animateStop methods simplify some of the complex
 * things you can do with Trident Timelines. You are not forced to use those
 * convenience methods; it's possible to set up your own Timelines.
 * <p>
 * This example uses a Timeline with a custom TimelineEase, which allows for
 * non-linear animation.
 */
public class mod_LSDDemoTridentTimeline extends BaseMod {
    public static final String ABOUT = "" +
            "Use a custom Timeline to animate a shape in a non-standard way:\n" +
            "Start fast and slow down as the shape approaches the ground.\n" +
            "/tp to x=0, z=0 to see the animation in action!";

    protected final LibShapeDraw libShapeDraw = new LibShapeDraw();

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        libShapeDraw.verifyInitialized();

        // Set up a wireframe cone pointing downward
        GLUCylinder shape = new GLUCylinder(
                new Vector3(14, 93, 0),
                Color.HOT_PINK.copy(),
                Color.HOT_PINK.copy().scaleAlpha(XrayShape.SECONDARY_ALPHA),
                2.5F, Float.MIN_NORMAL, 5.0F);
        shape.setSlices(12).setStacks(8).setWireframe(true, 3.0F);
        Vector3 vector = new Vector3();
        shape.addTransform(new ShapeTranslate(vector));
        shape.addTransform(new ShapeRotate(90.0, Axis.X)); // upright
        libShapeDraw.addShape(shape);

        // Animate it with a custom Timeline, using a Sine ease. This makes
        // the animation start out fast, then slow down at the end of each
        // cycle.
        // 
        // The final effect is a big pink arrow-like shape, dramatically
        // pointing at a specific block.
        Timeline timeline = new Timeline(vector);
        timeline.addPropertyToInterpolate("y", vector.getY(), vector.getY() - 30.0);
        timeline.setEase(new Sine());
        timeline.setDuration(3500);
        timeline.playLoop(RepeatBehavior.LOOP);
    }
}
