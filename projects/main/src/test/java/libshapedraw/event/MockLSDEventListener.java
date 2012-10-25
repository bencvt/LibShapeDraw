package libshapedraw.event;

import static org.junit.Assert.*;

import libshapedraw.LibShapeDraw;
import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;
import libshapedraw.primitive.Vector3;

public class MockLSDEventListener implements LSDEventListener {
    private int countRespawn = 0;
    private int countGameTick = 0;
    private int countPreRender = 0;

    public int getCountRespawn() {
        return countRespawn;
    }
    public int getCountGameTick() {
        return countGameTick;
    }
    public int getCountPreRender() {
        return countPreRender;
    }

    @Override
    public void onRespawn(LSDRespawnEvent event) {
        assertNotNull(event);
        assertTrue(event.getAPI() instanceof LibShapeDraw);
        assertTrue(event.getPlayerCoords() instanceof Vector3);
        assertNotNull(Boolean.valueOf(event.isNewDimension()));
        assertNotNull(Boolean.valueOf(event.isNewServer()));
        countRespawn++;
    }
    @Override
    public void onGameTick(LSDGameTickEvent event) {
        assertNotNull(event);
        assertTrue(event.getAPI() instanceof LibShapeDraw);
        assertTrue(event.getPlayerCoords() instanceof Vector3);
        countGameTick++;
    }
    @Override
    public void onPreRender(LSDPreRenderEvent event) {
        assertNotNull(event);
        assertTrue(event.getAPI() instanceof LibShapeDraw);
        assertTrue(event.getPlayerCoords() instanceof Vector3);
        assertNotNull(Boolean.valueOf(event.isGuiHidden()));
        countPreRender++;
    }

    public void reset() {
        countRespawn = 0;
        countGameTick = 0;
        countPreRender = 0;
    }
    public void assertCountsEqual(int expectedCountRespawn, int expectedCountGameTick, int expectedCountPreRender) {
        assertEquals(expectedCountRespawn, countRespawn);
        assertEquals(expectedCountGameTick, countGameTick);
        assertEquals(expectedCountPreRender, countPreRender);
    }
}
