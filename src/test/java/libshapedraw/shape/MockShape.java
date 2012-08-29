package libshapedraw.shape;

import org.lwjgl.opengl.GL11;

import libshapedraw.MinecraftAccess;
import libshapedraw.shape.Shape;

public class MockShape extends Shape {
    private int countRender = 0;

    public int getCountRender() {
        return countRender;
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
