package libshapedraw;

import static org.junit.Assert.*;
import libshapedraw.MinecraftAccess;
import libshapedraw.primitive.ReadonlyVector3;

public class MockMinecraftAccess implements MinecraftAccess {
    private boolean drawingStarted = false;
    private int numVertices = 0;

    @Override
    public void startDrawing(int mode) {
        assertFalse(isDrawingStarted());
        drawingStarted = true;
        assertEquals(getNumVertices(), 0);
        numVertices = 0;
    }

    @Override
    public void addVertex(double x, double y, double z) {
        assertTrue(isDrawingStarted());
        numVertices++;
    }

    @Override
    public void addVertex(ReadonlyVector3 coords) {
        assertTrue(isDrawingStarted());
        numVertices++;
    }

    @Override
    public void finishDrawing() {
        assertTrue(isDrawingStarted());
        drawingStarted = false;
        assertTrue(getNumVertices() > 0);
        numVertices = 0;
    }

    public boolean isDrawingStarted() {
        return drawingStarted;
    }
    public int getNumVertices() {
        return numVertices;
    }

    public void reset() {
        drawingStarted = false;
        numVertices = 0;
    }
}
