import libshapedraw.ApiInfo;
import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.Color;
import libshapedraw.shape.WireframeCuboid;

/**
 * Builds on mod_LSDDemoBasic.
 * <p>
 * Does the same thing, but validates that the API is loaded before
 * interacting with it.
 * <p>
 * If the API is not available, the mod still loads, though of course
 * it can't use any API features.
 */
public class mod_LSDDemoBasicDependSoft extends BaseMod {
    public static boolean isLibShapeDrawLoaded(String minVersion) {
        try {
            return ApiInfo.isVersionAtLeast(minVersion);
        } catch (LinkageError e) {
            return false;
        }
    }

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        if (!isLibShapeDrawLoaded("0.1")) {
            // in this example mod, libShapeDraw is a soft dependency, so we just return early.
            return;
        }
        LibShapeDraw libShapeDraw = new LibShapeDraw().verifyInitialized();
        WireframeCuboid box = new WireframeCuboid(1,63,0, 2,64,1);
        box.setLineStyle(Color.ROYAL_BLUE.copy(), 2.0F, true);
        libShapeDraw.getShapes().add(box);
    }
}
