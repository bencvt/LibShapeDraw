package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class MockShape extends Shape {
    private int countRender = 0;

    public int getCountRender() {
        return countRender;
    }

    @Override
    public void getOrigin(Vector3 buf) {
        buf.set(Vector3.ZEROS);
    }

    @Override
    public void renderShape(MinecraftAccess mc) {
        countRender++;
        mc.startDrawing(GL11.GL_LINES);
        mc.addVertex(0, 0, 0);
        mc.addVertex(1, 2, 3);
        mc.finishDrawing();
    }

    public void reset() {
        countRender = 0;
    }
}
