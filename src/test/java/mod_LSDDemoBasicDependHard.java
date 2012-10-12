import libshapedraw.LibShapeDraw;
import libshapedraw.primitive.Color;
import libshapedraw.shape.WireframeCuboid;

/**
 * Builds on mod_LSDDemoBasic.
 * <p>
 * Does the same thing, but validates that the API is loaded before
 * interacting with it.
 * <p>
 * Minecraft will still crash, but the crash report will be somewhat
 * more informative for the end user.
 */
public class mod_LSDDemoBasicDependHard extends BaseMod {
    public static boolean isLibShapeDrawLoaded(String minVersion) {
        try {
            return LibShapeDraw.isControllerInitialized() && LibShapeDraw.getVersion().compareTo(minVersion) >= 0;
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
            // in this example mod, libShapeDraw is a hard dependency, so refuse to load without it
            throw new RuntimeException("Missing dependency: LibShapeDraw v0.1+");
        }
        LibShapeDraw libShapeDraw = new LibShapeDraw().verifyInitialized();
        WireframeCuboid box = new WireframeCuboid(2,63,0, 3,64,1);
        box.setLineStyle(Color.AZURE.copy(), 2.0F, true);
        libShapeDraw.getShapes().add(box);
    }
}
