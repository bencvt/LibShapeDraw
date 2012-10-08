package libshapedraw;

import static org.junit.Assert.*;
import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;

public class MockMinecraftAccess implements MinecraftAccess {
    private boolean drawingStarted = false;
    private int curCountVertices = 0;
    private int countDraw = 0;
    private int countVertices = 0;
    private int countEnableStandardLighting = 0;

    public boolean isDrawingStarted() {
        return drawingStarted;
    }
    public int getCurCountVertices() {
        return curCountVertices;
    }
    public int getCountDraw() {
        return countDraw;
    }
    public int getCountVertices() {
        return countVertices;
    }
    public int getCountEnableStandardLighting() {
        return countEnableStandardLighting;
    }

    @Override
    public MinecraftAccess startDrawing(int mode) {
        assertFalse(isDrawingStarted());
        drawingStarted = true;
        assertEquals(curCountVertices, 0);
        curCountVertices = 0;
        return this;
    }

    @Override
    public MinecraftAccess addVertex(double x, double y, double z) {
        assertTrue(isDrawingStarted());
        curCountVertices++;
        countVertices++;
        return this;
    }

    @Override
    public MinecraftAccess addVertex(ReadonlyVector3 coords) {
        addVertex(coords.getX(), coords.getY(), coords.getZ());
        return this;
    }

    @Override
    public MinecraftAccess finishDrawing() {
        assertTrue(isDrawingStarted());
        drawingStarted = false;
        assertTrue(curCountVertices > 0);
        curCountVertices = 0;
        countDraw++;
        return this;
    }

    @Override
    public MinecraftAccess enableStandardItemLighting() {
        countEnableStandardLighting++;
        return this;
    }

    public void reset() {
        drawingStarted = false;
        curCountVertices = 0;
        countDraw = 0;
        countVertices = 0;
        countEnableStandardLighting = 0;
    }
    public void assertCountsEqual(int expectedCountDraw, int expectedCountVertices, boolean expectDouble) {
        if (expectDouble) {
            assertEquals(expectedCountDraw*2, countDraw);
            assertEquals(expectedCountVertices*2, countVertices);
        } else {
            assertEquals(expectedCountDraw, countDraw);
            assertEquals(expectedCountVertices, countVertices);
        }
    }
}
