package libshapedraw.internal;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import libshapedraw.LibShapeDraw;
import libshapedraw.MockMinecraftAccess;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.Vector3;
import libshapedraw.shape.WireframeLine;

import org.junit.Before;
import org.junit.Test;

public class TestController extends SetupTestEnvironment.TestCase {
    private Controller ct;

    @Before
    public void initController() {
        ct = Controller.getInstance();
    }

    @Test
    public void testLog() {
        assertNotNull(Controller.getLog());
        assertTrue(Controller.getLog() instanceof Util.FileLogger);
    }

    @Test
    public void testIsInitialized() {
        // initialization done by SetupTestEnvironment
        assertTrue(Controller.isInitialized());
    }

    @Test(expected=IllegalStateException.class)
    public void testInitializeTwice() {
        // first initialization done by SetupTestEnvironment
        ct.initialize(new MockMinecraftAccess());
    }

    @Test(expected=IllegalStateException.class)
    public void testRegisterApiInstanceInvalidExists() {
        // LibShapeDraw's takes care of registering itself with the internal controller...
        LibShapeDraw api = new LibShapeDraw();
        // ...so it's an error to reregister it.
        ct.registerApiInstance(api, "wat");
    }

    @Test(expected=NullPointerException.class)
    public void testRegisterApiInstanceInvalidNull() {
        ct.registerApiInstance(null, "wat");
    }

    @Test
    public void testUnregisterApiInstance() {
        LibShapeDraw api0 = new LibShapeDraw();
        // Client code should NOT do this as the Controller is an internal class.
        // Rather, use api.unregister().
        assertTrue(ct.unregisterApiInstance(api0));
        assertFalse(ct.unregisterApiInstance(api0));
        assertFalse(ct.unregisterApiInstance(api0));

        LibShapeDraw api1 = new LibShapeDraw();
        assertTrue(api1.unregister());
        assertFalse(api1.unregister());
        assertFalse(api1.unregister());

        assertFalse(ct.unregisterApiInstance(null));
    }

    @Test
    public void testReregisterApiInstance() {
        // Client code should NOT do this as the Controller is an internal class.
        LibShapeDraw api = new LibShapeDraw();
        assertTrue(ct.unregisterApiInstance(api));
        ct.registerApiInstance(api, "whatever");
    }

    @Test
    public void testMethods() {
        // none of these calls should throw anything
        assertTrue(Controller.isInitialized());
        ct.dump();
        ct.gameTick(Vector3.ZEROS);
        ct.render(Vector3.ZEROS, false, false);
        ct.render(Vector3.ZEROS, true, false);
        ct.respawn(Vector3.ZEROS, false, false);
        ct.respawn(Vector3.ZEROS, false, true);
        ct.respawn(Vector3.ZEROS, true, false);
        ct.respawn(Vector3.ZEROS, true, true);
    }

    @Test
    public void testAlternateRenderingHook() {
        mockMinecraftAccess.reset();
        new LibShapeDraw().addShape(new WireframeLine(0.0, 0.0, 0.0, 0.0, 1.0, 0.0).setLineStyle(Color.WHITE.copy(), 1.0F, false));
        assertEquals(0, mockMinecraftAccess.getCountDraw());
        ct.render(Vector3.ZEROS, false, false);
        assertEquals(1, mockMinecraftAccess.getCountDraw());

        // permanently disables the default render hook
        ct.render(Vector3.ZEROS, false, true);
        assertEquals(2, mockMinecraftAccess.getCountDraw());

        // subsequent render calls without the isAlternateRenderHook flag should be ignored
        ct.render(Vector3.ZEROS, false, false);
        assertEquals(2, mockMinecraftAccess.getCountDraw());

        ct.render(Vector3.ZEROS, false, true);
        assertEquals(3, mockMinecraftAccess.getCountDraw());

        // restore the default render hook so we don't affect other test cases
        try {
            Field field = Controller.class.getDeclaredField("useAlternateRenderHook");
            field.setAccessible(true);
            field.set(ct, false);
        } catch (Exception e) {
            throw new RuntimeException("internal reflection error", e);
        }
        ct.render(Vector3.ZEROS, false, false);
        assertEquals(4, mockMinecraftAccess.getCountDraw());
    }
}
