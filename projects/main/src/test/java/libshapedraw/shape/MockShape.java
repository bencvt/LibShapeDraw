package libshapedraw.shape;

import static org.junit.Assert.assertEquals;
import libshapedraw.LibShapeDraw;
import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.Vector3;

import org.lwjgl.opengl.GL11;

public class MockShape extends Shape {
    private int countOnAdd;
    private int countOnRemove;
    private int countOnPreRender;
    private int countOnPostRender;
    private int countRender;

    public MockShape() {
        super(Vector3.ZEROS.copy());
    }

    @Override
    public void onAdd(LibShapeDraw apiInstance) {
        super.onAdd(apiInstance);
        countOnAdd++;
    }

    @Override
    public void onRemove(LibShapeDraw apiInstance) {
        super.onRemove(apiInstance);
        countOnRemove++;
    }

    @Override
    public void onPreRender(MinecraftAccess mc) {
        super.onPreRender(mc);
        countOnPreRender++;
    }

    @Override
    public void onPostRender(MinecraftAccess mc) {
        super.onPostRender(mc);
        countOnPostRender++;
    }

    @Override
    public void renderShape(MinecraftAccess mc) {
        countRender++;
        mc.startDrawing(GL11.GL_LINES);
        mc.addVertex(0, 0, 0);
        mc.addVertex(1, 2, 3);
        mc.finishDrawing();
    }

    public int getCountOnAdd() {
        return countOnAdd;
    }

    public int getCountOnRemove() {
        return countOnRemove;
    }

    public int getCountOnPreRender() {
        assertRendersCountsEqual();
        return countRender;
    }

    public int getCountOnPostRender() {
        assertRendersCountsEqual();
        return countRender;
    }

    public int getCountRender() {
        assertRendersCountsEqual();
        return countRender;
    }

    public void assertAddRemoveCounts(int expectedOnAdd, int expectedOnRemove) {
        assertEquals(expectedOnAdd, countOnAdd);
        assertEquals(expectedOnRemove, countOnRemove);
    }

    public void assertRendersCountsEqual() {
        assertEquals(countRender, countOnPreRender);
        assertEquals(countRender, countOnPostRender);
    }

    public void resetCounts() {
        countOnAdd = 0;
        countOnRemove = 0;
        countOnPreRender = 0;
        countOnPostRender = 0;
        countRender = 0;
    }
}
