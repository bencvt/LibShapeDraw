package libshapedraw.shape;

import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class MockShape extends Shape {
    public MockShape() {
        super(Vector3.ZEROS.copy());
    }

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

    public void resetCounts() {
        countRender = 0;
    }
}
