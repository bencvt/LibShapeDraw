package libshapedraw.demos;

import java.util.ArrayList;

import libshapedraw.LibShapeDraw;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;
import libshapedraw.shape.WireframeLines;
import libshapedraw.transform.ShapeRotate;

import org.lwjgl.input.Keyboard;

/**
 * Demonstrates how LibShapeDraw can be used to draw arbitrary shapes: in this
 * case, the word "LibShapeDraw" in a handwritten font.
 */
public class mod_LSDDemoLogo extends BaseMod implements LSDEventListener {
    public static final String ABOUT = "" +
            "Draw arbitrary shapes with LibShapeDraw.\n" +
            "Press Z/X/C!";

    // path data from a freehand SVG drawing
    private static final int[][] LINE_DATA = {
        {-496,131, -500,99, -506,63, -518,15, -526,-29, -528,-49, -522,-57, -482,-59, -438,-53},
        {-416,17, -422,-51},
        {-432,53, -430,39, -416,37, -408,51, -410,63, -428,67, -430,59, -432,53},
        {-390,121, -394,23, -396,-17, -406,-57, -380,-59, -354,-53, -336,-25, -336,-9, -344,19, -360,25, -376,25, -388,19},
        {-220,85, -234,105, -254,113, -280,111, -300,99, -308,71, -300,47, -268,25, -238,19, -224,1, -222,-21, -240,-43, -270,-47, -296,-47, -316,-37, -322,-11},
        {-200,115, -194,41, -192,5, -200,-43, -202,-59},
        {-192,-1, -180,9, -174,9, -154,9, -140,-1, -138,-33, -138,-59},
        {-122,13, -104,19, -86,19, -74,9, -64,-19, -64,-49, -54,-61},
        {-64,-49, -76,-57, -82,-59, -100,-59, -108,-53, -114,-37, -110,-23, -92,-17, -74,-19, -68,-19},
        {-30,-49, -20,-53, 6,-51, 22,-29, 24,9, 12,27, -8,31, -34,23, -38,1, -34,-51, -24,-105, -24,-131},
        {48,-5, 70,-5, 110,1, 114,19, 100,43, 92,43, 72,49, 56,39, 44,11, 44,-7, 52,-43, 72,-55, 96,-55, 112,-49, 118,-35},
        {100,109, 122,125, 162,127, 194,119, 222,93, 230,69, 242,25, 240,-21, 218,-53, 198,-67, 168,-69, 140,-65, 146,11, 156,83, 154,121},
        {254,27, 262,1, 262,-27, 258,-61},
        {262,5, 272,19, 290,23, 316,17},
        {324,11, 342,19, 358,19, 382,3, 384,-29, 378,-55, 378,-69, 388,-75},
        {378,-65, 354,-69, 336,-65, 320,-49, 320,-29, 330,-21, 356,-19, 376,-23},
        {392,19, 408,9, 418,-37, 418,-57, 418,-71, 444,-45, 458,-19, 458,-1, 470,-27, 472,-43, 472,-61, 470,-69, 506,-31, 514,-5, 518,9, 528,17},
    };
    private static final double SCALE = 0.015;

    private LibShapeDraw libShapeDraw;
    private LineStyle lineStyle;
    private Vector3 origin;
    private ShapeRotate shapeRotate;
    private long lastKeyhit;

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        libShapeDraw = new LibShapeDraw()
        .verifyInitialized()
        .addEventListener(this)
        .setVisible(false)
        .setVisibleWhenHidingGui(true);

        lineStyle = new LineStyle(Color.WHITE.copy(), 5.0F, true);
        shapeRotate = new ShapeRotate(0, 0, 1, 0);

        origin = Vector3.ZEROS.copy();
        for (int[] pairs : LINE_DATA) {
            ArrayList<ReadonlyVector3> vec = new ArrayList<ReadonlyVector3>(pairs.length / 2);
            for (int i = 0; i < pairs.length; i += 2) {
                vec.add(new Vector3(pairs[i]*SCALE, pairs[i+1]*SCALE, 0));
            }
            libShapeDraw.addShape(
                    new WireframeLines(origin, vec)
                    .setLineStyle(lineStyle)
                    .addTransform(shapeRotate));
        }
    }

    @Override
    public void onRespawn(LSDRespawnEvent event) {
        libShapeDraw.setVisible(false);
    }

    @Override
    public void onGameTick(LSDGameTickEvent event) {
        // do nothing
    }

    @Override
    public void onPreRender(LSDPreRenderEvent event) {
        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
            if (keyHitTooEarly()) {
                return;
            }
            libShapeDraw.setVisible(true);
            origin.set(event.getPlayerCoords()).addX(5);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_X)) {
            if (keyHitTooEarly()) {
                return;
            }
            shapeRotate.setAngle(Math.random()*360);
            shapeRotate.getAxis().setX(Math.random()).setY(Math.random()).setZ(Math.random());
        } else if (Keyboard.isKeyDown(Keyboard.KEY_C)) {
            if (keyHitTooEarly()) {
                return;
            }
            lineStyle.getMainColor()
            .setRed(Math.random())
            .setGreen(Math.random())
            .setBlue(Math.random())
            .setAlpha(0.5 + Math.random()*0.5);
            lineStyle.getSecondaryColor().set(lineStyle.getMainColor().copy().scaleAlpha(0.25));
        }
    }

    private boolean keyHitTooEarly() {
        long now = System.currentTimeMillis();
        if (now < lastKeyhit + 250) {
            return true;
        }
        lastKeyhit = now;
        return false;
    }
}
