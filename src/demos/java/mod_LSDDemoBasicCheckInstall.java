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
public class mod_LSDDemoBasicCheckInstall extends BaseMod {
    public static boolean isLibShapeDrawLoaded(String minVersion) {
        try {
            return ApiInfo.isVersionAtLeast(minVersion);
        } catch (LinkageError e) {
            return false;
        }
    }

    boolean missingApi;

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        if (!isLibShapeDrawLoaded("0.1")) {
            // Gracefully handle the missing dependency: set a flag and exit
            // early before instantiating any LibShapeDraw classes.
            //
            // We could check this flag later on and give the user a useful
            // message, explaining that this mod (or at least its features that
            // depend on LibShapeDraw) are disabled.
            //
            // Much more user friendly than crashing.
            missingApi = true;
            return;
        }
        LibShapeDraw libShapeDraw = new LibShapeDraw().verifyInitialized();
        WireframeCuboid box = new WireframeCuboid(1,63,0, 2,64,1);
        box.setLineStyle(Color.ROYAL_BLUE.copy(), 2.0F, true);
        libShapeDraw.getShapes().add(box);
    }
}
