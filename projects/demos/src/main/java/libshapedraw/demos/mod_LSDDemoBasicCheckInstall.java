package libshapedraw.demos;

import libshapedraw.ApiInfo;
import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.Color;
import libshapedraw.shape.WireframeCuboid;

/**
 * Builds on mod_LSDDemoBasic.
 * <p>
 * Does the same thing, but validates that the LibShapeDraw API is loaded
 * before interacting with it.
 * <p>
 * If the API is not available, the mod still loads, though of course
 * it can't use any API features.
 */
public class mod_LSDDemoBasicCheckInstall extends BaseMod {
    public static final String ABOUT = "" +
            "Shows how to gracefully handle missing dependencies.\n" +
            "Not much to see in-game; see the source code!";

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

        // One other worthwhile sanity check is to call the verifyInitialized()
        // method, which ensures that ModLoader or Forge has successfully
        // initialized LibShapeDraw. See the method's Javadoc for more details.
        LibShapeDraw libShapeDraw = new LibShapeDraw().verifyInitialized();
        WireframeCuboid box = new WireframeCuboid(2,63,0, 3,64,1);
        box.setLineStyle(Color.ROYAL_BLUE.copy(), 2.0F, true);
        libShapeDraw.addShape(box);
    }
}
