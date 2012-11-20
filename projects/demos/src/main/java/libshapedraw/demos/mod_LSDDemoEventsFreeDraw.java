package libshapedraw.demos;

import libshapedraw.LibShapeDraw;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;

import org.lwjgl.opengl.GL11;

/**
 * Illustrates that you are not obliged to use any part of the LibShapeDraw API
 * that you don't need: pick and choose the components you want to use!
 * <p>
 * In this example, we don't add any Shapes. Rather, we just use the rendering
 * hook (i.e., the onPreRender event) to manually draw using direct OpenGL
 * calls.
 * <p>
 * Of course, direct OpenGL can be painful. If you have a graphics library that
 * runs on top of OpenGL, and you just need a rendering hook, you can use this
 * same technique of drawing in onPreRender.
 */
public class mod_LSDDemoEventsFreeDraw extends BaseMod implements LSDEventListener {
    public static final String ABOUT = "" +
            "Manually drawing a shape using direct OpenGL calls.\n" +
            "/tp to x=0, z=0 to see the shape!";

    @Override
    public String getVersion() {
        return "demo";
    }

    @Override
    public void load() {
        new LibShapeDraw().verifyInitialized().addEventListener(this);
    }

    @Override
    public void onRespawn(LSDRespawnEvent event) {
        // do nothing
    }

    @Override
    public void onGameTick(LSDGameTickEvent event) {
        // do nothing
    }

    @Override
    public void onPreRender(LSDPreRenderEvent event) {
        // Manually set up the OpenGL context.
        // 
        // Even though we're not using Shapes directly, we could use its static
        // helper method Shape.glContextStandardSetup() to do most of this.
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_LIGHT0);
        GL11.glDisable(GL11.GL_LIGHT1);
        GL11.glDisable(GL11.GL_COLOR_MATERIAL);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glColor4d(1.0, 1.0, 0.5, 0.8);
        GL11.glLineWidth(2.0F);
        GL11.glEnable(GL11.GL_LINE_STIPPLE);
        GL11.glLineStipple(3, (short) 0xaaaa);

        // Render a square with dotted lines.
        final double x = -3.0;
        final double y = 65.0;
        final double z = -3.0;
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex3d(x,        y, z);
        GL11.glVertex3d(x + 10.0, y, z);
        GL11.glVertex3d(x + 10.0, y, z + 10.0);
        GL11.glVertex3d(x,        y, z + 10.0);
        GL11.glEnd();

        // Don't forget to clean up context settings that might affect elements
        // that still need to be rendered!
        GL11.glDisable(GL11.GL_LINE_STIPPLE);
    }
}
