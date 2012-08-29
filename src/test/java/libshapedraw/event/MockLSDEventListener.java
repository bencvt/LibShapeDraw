package libshapedraw.event;

import static org.junit.Assert.*;

import libshapedraw.event.LSDEventListener;
import libshapedraw.event.LSDGameTickEvent;
import libshapedraw.event.LSDPreRenderEvent;
import libshapedraw.event.LSDRespawnEvent;

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

    public void onRespawn(LSDRespawnEvent event) {
        countRespawn++;
    }
    public void onGameTick(LSDGameTickEvent event) {
        countGameTick++;
    }
    public void onPreRender(LSDPreRenderEvent event) {
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
