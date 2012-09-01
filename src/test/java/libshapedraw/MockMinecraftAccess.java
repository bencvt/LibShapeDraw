package libshapedraw;

import static org.junit.Assert.*;
import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;

public class MockMinecraftAccess implements MinecraftAccess {
    private boolean drawingStarted = false;
    private int curCountVertices = 0;
    private int countDraw = 0;
    private int countVertices = 0;

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

    @Override
    public void startDrawing(int mode) {
        assertFalse(isDrawingStarted());
        drawingStarted = true;
        assertEquals(curCountVertices, 0);
        curCountVertices = 0;
    }

    @Override
    public void addVertex(double x, double y, double z) {
        assertTrue(isDrawingStarted());
        curCountVertices++;
        countVertices++;
    }

    @Override
    public void addVertex(ReadonlyVector3 coords) {
        assertTrue(isDrawingStarted());
        curCountVertices++;
        countVertices++;
    }

    @Override
    public void finishDrawing() {
        assertTrue(isDrawingStarted());
        drawingStarted = false;
        assertTrue(curCountVertices > 0);
        curCountVertices = 0;
        countDraw++;
    }

    public void reset() {
        drawingStarted = false;
        curCountVertices = 0;
        countDraw = 0;
        countVertices = 0;
    }
    public void assertCountsEqual(int expectedCountDraw, int expectedCountVertices) {
        assertEquals(expectedCountDraw, countDraw);
        assertEquals(expectedCountVertices, countVertices);
    }
}
