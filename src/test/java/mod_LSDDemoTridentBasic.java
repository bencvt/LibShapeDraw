import libshapedraw.LibShapeDraw;
import libshapedraw.animation.trident.Timeline;
import libshapedraw.animation.trident.Timeline.RepeatBehavior;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.ReadonlyColor;
import libshapedraw.shape.WireframeCuboid;
import libshapedraw.transform.ShapeRotate;
import libshapedraw.transform.ShapeScale;

/**
 * Create some shapes and animate them using the built-in Trident animation
 * library. Refer to README-Trident.md for more information.
 * <p>
 * Using Trident is the recommended method for animating Shapes, but it's not
 * the only way. You could, for example, manually update Vector3s/Colors/
 * ShapeTransforms in the LSDEventListener.onPreRender method.
 * <p>
 * Using Trident is worthwhile though: the end result is a lot less boilerplate
 * code for you to write. Also, Trident uses threading internally for better
 * performance.
 */
public class mod_LSDDemoTridentBasic extends BaseMod {
    protected LibShapeDraw libShapeDraw = new LibShapeDraw();

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        // The Shapes will be created and their animations will start as soon
        // as ModLoader loads this mod. That means the Shapes are getting
        // updated even if we're on the main menu or on a loading screen.
        // 
        // This is a relatively harmless waste of a few CPU cycles. Trident
        // runs at 25 FPS, updating the Shapes at most every 40 milliseconds.
        // And in this case it's less than a dozen scalar values being updated.
        // 
        // If you're creating a lot of Shapes and associated Timelines
        // dynamically, a cleaner design will involve managing the Timelines
        // yourself. @see mod_LSDDemoTridentDynamic
        createBoxRotating();
        createBoxColorShifting();
        createBoxGrowing();
    }

    private void createBoxRotating() {
        WireframeCuboid box = new WireframeCuboid(8,63,0, 9,64,1);
        box.setLineStyle(Color.DODGER_BLUE.copy(), 3.0F, true);
        ShapeRotate rotate = new ShapeRotate(0.0, 0.0, 1.0, 0.0);
        box.addTransform(rotate);
        libShapeDraw.addShape(box);

        Timeline timeline = new Timeline(rotate);
        timeline.addPropertyToInterpolate("angle", 0.0, 360.0);
        timeline.setDuration(5000);
        timeline.playLoop(RepeatBehavior.LOOP);
    }

    private void createBoxColorShifting() {
        ReadonlyColor fromColor = Color.CRIMSON;
        ReadonlyColor toColor = Color.MEDIUM_BLUE.copy().setAlpha(0.2);

        WireframeCuboid box = new WireframeCuboid(8,63,2, 9,64,3);
        box.setLineStyle(fromColor.copy(), 3.0F, false);
        libShapeDraw.addShape(box);

        Timeline timeline = new Timeline(box.getLineStyle().getMainColor());
        timeline.addPropertyToInterpolate("red",   fromColor.getRed(),   toColor.getRed());
        timeline.addPropertyToInterpolate("blue",  fromColor.getBlue(),  toColor.getBlue());
        timeline.addPropertyToInterpolate("green", fromColor.getGreen(), toColor.getGreen());
        timeline.addPropertyToInterpolate("alpha", fromColor.getAlpha(), toColor.getAlpha());
        timeline.setDuration(750);
        timeline.playLoop(RepeatBehavior.REVERSE);
    }

    private void createBoxGrowing() {
        WireframeCuboid box = new WireframeCuboid(8,63,4, 9,64,5);
        box.setLineStyle(Color.GOLD.copy(), 3.0F, true);
        ShapeScale scale = new ShapeScale();
        box.addTransform(scale);
        libShapeDraw.addShape(box);

        Timeline timeline = new Timeline(scale.getScaleXYZ());
        timeline.addPropertyToInterpolate("x", 0.5F, 2.0F);
        timeline.addPropertyToInterpolate("z", 2.0F, 0.5F);
        timeline.setDuration(4500);
        timeline.playLoop(RepeatBehavior.REVERSE);

        // Since this is a simple WireframeCuboid, we could have interpolated on
        // getLowerCorner() and getUpperCorner() instead of using a ShapeScale
        // OpenGL transform. However it's generally better to let OpenGL handle
        // stuff whenever possible.
    }
}
