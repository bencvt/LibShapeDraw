package libshapedraw.shape;

import static org.junit.Assert.assertEquals;
import libshapedraw.LibShapeDraw;
import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class MockShape extends Shape {
    private int countRender;
    private int countOnAdd;
    private int countOnRemove;

    public MockShape() {
        super(Vector3.ZEROS.copy());
    }

    @Override
    public void onAdd(LibShapeDraw apiInstance) {
        countOnAdd++;
    }

    @Override
    public void onRemove(LibShapeDraw apiInstance) {
        countOnRemove++;
    }

    @Override
    public void renderShape(MinecraftAccess mc) {
        countRender++;
        mc.startDrawing(GL11.GL_LINES);
        mc.addVertex(0, 0, 0);
        mc.addVertex(1, 2, 3);
        mc.finishDrawing();
    }

    public int getCountRender() {
        return countRender;
    }

    public int getCountOnAdd() {
        return countOnAdd;
    }

    public int getCountOnRemove() {
        return countOnRemove;
    }

    public void assertAddRemoveCounts(int expectedOnAdd, int expectedOnRemove) {
        assertEquals(expectedOnAdd, countOnAdd);
        assertEquals(expectedOnRemove, countOnRemove);
    }

    public void resetCounts() {
        countRender = 0;
        countOnAdd = 0;
        countOnRemove = 0;
    }
}
