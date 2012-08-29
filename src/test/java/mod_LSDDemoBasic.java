import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.Color;
import libshapedraw.shape.WireframeCuboid;

/**
 * A bare-bones example, sets up a 1x1x1 cyan wireframe cube at x=0, y=63 (sea level), z=0
 */
public class mod_LSDDemoBasic extends BaseMod {
    protected LibShapeDraw libShapeDraw;

    public mod_LSDDemoBasic() {
        libShapeDraw = new LibShapeDraw();
        WireframeCuboid zeroMarker = new WireframeCuboid(0,63,0, 1,64,1);
        zeroMarker.setLineStyle(Color.CYAN.copy(), 2.0F, true);
        libShapeDraw.addShape(zeroMarker);
        // We could have done the above in a single line:
        //new LibShapeDraw().addShape(new WireframeCuboid(0,63,0, 1,64,1).setLineStyle(Color.CYAN.copy(), 2.0F, true));
    }

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        // Do nothing.
        // We could have just as easily set up the API here instead of in the constructor.
    }
}
