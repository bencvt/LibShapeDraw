package libshapedraw.demos;

import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.Color;
import libshapedraw.shape.WireframeCuboid;

/**
 * A bare-bones example, sets up a 1x1x1 cyan wireframe cube at
 * x=0, y=63 (sea level), z=0.
 */
public class mod_LSDDemoBasic extends BaseMod {
    public static final String ABOUT = "" +
            "A bare-bones example.\n" +
            "/tp to x=0, z=0 to see the shape!";

    private LibShapeDraw libShapeDraw;

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        // We could have just as easily set up the API in the constructor
        // instead; LibShapeDraw is designed to be as flexible as possible.
        libShapeDraw = new LibShapeDraw();
        WireframeCuboid zeroMarker = new WireframeCuboid(0,63,0, 1,64,1);
        zeroMarker.setLineStyle(Color.CYAN.copy(), 2.0F, true);
        libShapeDraw.addShape(zeroMarker);

        // verifyInitialized() is the only method that shouldn't be called in
        // the constructor. Always wait until BaseMod.load() or later for this;
        // mod_LibShapeDraw needs a chance to load too!
        //
        // Note that we don't *have* to call verifyInitialized. It's just a
        // good idea to do so, as it will throw an exception if LibShapeDraw is
        // not installed properly. This should normally never happen, but
        // sanity checks are good.
        libShapeDraw.verifyInitialized();

        // Prefer a functional coding style? This is the equivalent of the above:
        //libShapeDraw = new LibShapeDraw()
        //    .addShape(
        //        new WireframeCuboid(0,63,0, 1,64,1)
        //        .setLineStyle(Color.CYAN.copy(), 2.0F, true))
        //    .verifyInitialized();
    }
}
