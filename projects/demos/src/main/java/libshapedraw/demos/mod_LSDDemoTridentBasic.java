package libshapedraw.demos;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.Axis;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;
import libshapedraw.shape.GLUSphere;
import libshapedraw.shape.WireframeCuboid;
import libshapedraw.transform.ShapeRotate;
import libshapedraw.transform.ShapeScale;

/**
 * Create some shapes and animate them using the animateStart/animateStop
 * convenience methods.
 * <p>
 * Under the hood, these methods are creating Trident Timeline instances,
 * setting up property interpolators, running an animation engine thread, and
 * other fun stuff. Refer to README-Trident.md for more information.
 * <p>
 * This isn't the only possible way to animate Shapes. You could, for example,
 * manually update Vector3s/Colors/ShapeTransforms using the onPreRender event.
 * However, using animateStart/animateStop is the recommended way to animate
 * Shapes. It eliminates a lot of boilerplate code.
 */
public class mod_LSDDemoTridentBasic extends BaseMod {
    public static final String ABOUT = "" +
            "Animate some shapes using the Trident animation library.\n" +
            "/tp to x=0, z=0 to see the shapes in action!";

    protected LibShapeDraw libShapeDraw = new LibShapeDraw();

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        libShapeDraw.verifyInitialized();
        // The Shapes will be created and their animations will start as soon
        // as ModLoader loads this mod. That means the Shapes are getting
        // updated even if we're on the main menu or on a loading screen.
        // 
        // This is a relatively harmless waste of a few CPU cycles. Trident
        // runs at 25 FPS, updating the Shapes at most every 40 milliseconds.
        // And in this case it's less than a dozen scalar values being updated.
        // 
        // If you're dynamically creating a lot of Shapes with associated
        // looping animations, a cleaner design will involve keeping track of
        // the animations. @see mod_LSDDemoTridentDynamic
        createRotatingShape();
        createColorShiftingShape();
        createResizingShape();
    }

    private void createRotatingShape() {
        GLUSphere sphere = new GLUSphere(new Vector3(8, 63, 0),
                Color.DODGER_BLUE.copy(),
                Color.DODGER_BLUE.copy().setAlpha(0.25),
                1.0F);
        sphere.setSlices(8).setStacks(8).setWireframe(true, 3.0F);
        ShapeRotate rotate = new ShapeRotate(0.0, Axis.Y);
        sphere.addTransform(rotate);
        libShapeDraw.addShape(sphere);
        rotate.animateStartLoop(360.0, false, 5000);
    }

    private void createColorShiftingShape() {
        WireframeCuboid box = new WireframeCuboid(8,63,2, 9,64,3);
        Color color = Color.CRIMSON.copy();
        box.setLineStyle(color, 3.0F, false);
        libShapeDraw.addShape(box);
        color.animateStartLoop(Color.MEDIUM_BLUE.copy().setAlpha(0.2), true, 750);

        // Prefer a functional coding style? This is the equivalent of the above:
        //libShapeDraw.addShape(
        //        new WireframeCuboid(8,63,2, 9,64,3)
        //        .setLineStyle(
        //                Color.CRIMSON.copy()
        //                .animateStartLoop(Color.MEDIUM_BLUE.copy().setAlpha(0.2), true, 750),
        //                3.0F, false));
    }

    private void createResizingShape() {
        WireframeCuboid box = new WireframeCuboid(8,63,4, 9,64,5);
        box.setLineStyle(Color.GOLD.copy(), 3.0F, true);
        ShapeScale scale = new ShapeScale(0.5, 1.0, 2.0);
        box.addTransform(scale);
        libShapeDraw.addShape(box);
        scale.animateStartLoop(2.0, 1.0, 0.5, true, 4500);

        // Since this is a simple WireframeCuboid, we could have animated
        // box.getLowerCorner() and box.getUpperCorner() instead of using a
        // ShapeScale OpenGL transform. Either method is valid, but simplicity
        // is good.
    }
}
