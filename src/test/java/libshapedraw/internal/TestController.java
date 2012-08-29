package libshapedraw.internal;

import static org.junit.Assert.*;
import libshapedraw.MockMinecraftAccess;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.internal.Controller;
import libshapedraw.internal.Util;
import libshapedraw.primitive.Vector3;

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

    @Test(expected=IllegalStateException.class)
    public void testInitializeTwice() {
        // first initialization done by SetupTestEnvironment
        ct.initialize(new MockMinecraftAccess());
    }

    @Test
    public void testMethods() {
        // none of these calls should throw anything
        assertTrue(Controller.isInitialized());
        ct.dump();
        ct.gameTick(new Vector3(0, 0, 0));
        ct.render(new Vector3(0, 0, 0), false);
        ct.render(new Vector3(0, 0, 0), true);
        ct.respawn(new Vector3(0, 0, 0), false, false);
        ct.respawn(new Vector3(0, 0, 0), false, true);
        ct.respawn(new Vector3(0, 0, 0), true, false);
        ct.respawn(new Vector3(0, 0, 0), true, true);
    }
}
