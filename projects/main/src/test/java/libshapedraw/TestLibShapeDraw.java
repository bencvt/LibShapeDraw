package libshapedraw;

import static org.junit.Assert.*;

import libshapedraw.LibShapeDraw;
import libshapedraw.event.MockLSDEventListener;
import libshapedraw.internal.LSDController;
import libshapedraw.primitive.Vector3;
import libshapedraw.shape.MockShape;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestLibShapeDraw extends SetupTestEnvironment.TestCase {
    private LibShapeDraw lib;

    @Before
    public void registerApi() {
        lib = new LibShapeDraw();
    }

    @After
    public void unregisterApi() {
        if (lib != null) {
            lib.unregister();
            lib = null;
        }
    }

    private void renderCheck(boolean expectedToRender, MockShape counter, boolean hideGui) {
        int expectedCount = counter.getCountRender() + (expectedToRender ? 1 : 0);
        render(hideGui);
        assertEquals(expectedCount, counter.getCountRender());
    }
    private void render(boolean hideGui) {
        LSDController.getInstance().render(new Vector3(0,0,0), hideGui);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testVersion() {
        assertFalse(LibShapeDraw.getVersion().isEmpty());
    }

    @Test
    public void testUnregister() {
        MockShape counter = new MockShape();
        lib.addShape(counter);
        renderCheck(true, counter, false);

        assertTrue(lib.unregister());

        // once unregistered, the API instance is no longer updated by the controller
        renderCheck(false, counter, false);

        assertFalse(lib.unregister()); // already unregistered
        assertFalse(lib.unregister()); // still unregistered
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testIsControllerInitialized() {
        assertTrue(LibShapeDraw.isControllerInitialized());
        lib.verifyInitialized(); // no exception thrown
    }

    @Test
    public void testGetMinecraftAccess() {
        assertNotNull(lib.getMinecraftAccess());
    }

    @Test
    public void testDebugDump() {
        assertTrue(lib.debugDump());
    }

    // ----------------------------------------------------------------------
    // Shapes
    // ----------------------------------------------------------------------

    @Test
    public void testShapesAdd() {
        assertEquals(0, lib.getShapes().size());
        lib.addShape(new MockShape());
        assertEquals(1, lib.getShapes().size());
        lib.addShape(new MockShape());
        assertEquals(2, lib.getShapes().size());
        lib.addShape(new MockShape()).addShape(new MockShape()); // method chaining
        assertEquals(4, lib.getShapes().size());
        render(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testShapesAddNull() {
        lib.addShape(null);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testShapesUnmodifiable() {
        lib.getShapes().add(new MockShape());
    }

    @Test
    public void testShapesAddDupe() {
        MockShape dupe = new MockShape();
        lib.addShape(dupe);
        assertEquals(1, lib.getShapes().size());
        lib.addShape(dupe);
        assertEquals(1, lib.getShapes().size());
    }

    @Test
    public void testShapesRemoveClear() {
        MockShape shape0 = new MockShape();
        MockShape shape1 = new MockShape();
        MockShape shape2 = new MockShape();
        MockShape shape3 = new MockShape();
        lib.addShape(shape0).addShape(shape1).addShape(shape2).addShape(shape3);
        assertEquals(4, lib.getShapes().size());

        lib.removeShape(shape0);
        assertEquals(3, lib.getShapes().size());

        lib.removeShape(shape0);
        assertEquals(3, lib.getShapes().size());
        lib.removeShape(new MockShape());
        assertEquals(3, lib.getShapes().size());
        lib.removeShape(null);
        assertEquals(3, lib.getShapes().size());

        lib.removeShape(shape1).removeShape(shape2);
        assertEquals(1, lib.getShapes().size());
        assertTrue(lib.getShapes().contains(shape3));

        lib.clearShapes();
        assertEquals(0, lib.getShapes().size());
    }

    @Test
    public void testShapesRemoveInvalid() {
        MockShape shape = new MockShape();
        assertFalse(lib.getShapes().contains(shape));
        lib.removeShape(shape); // allowed no-op

        lib.removeShape(null); // also allowed
    }

    // ----------------------------------------------------------------------
    // Event Listeners
    // ----------------------------------------------------------------------

    @Test
    public void testEventListenersAdd() {
        assertEquals(0, lib.getEventListeners().size());
        lib.addEventListener(new MockLSDEventListener());
        assertEquals(1, lib.getEventListeners().size());
        lib.addEventListener(new MockLSDEventListener());
        assertEquals(2, lib.getEventListeners().size());
        lib.addEventListener(new MockLSDEventListener()).addEventListener(new MockLSDEventListener()); // method chaining
        assertEquals(4, lib.getEventListeners().size());
        render(false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testEventListenersAddNull() {
        lib.addEventListener(null);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testEventListenersUnmodifiable() {
        lib.getEventListeners().add(new MockLSDEventListener());
    }

    @Test
    public void testEventListenersAddDupe() {
        MockLSDEventListener dupe = new MockLSDEventListener();
        lib.addEventListener(dupe);
        assertEquals(1, lib.getEventListeners().size());
        lib.addEventListener(dupe);
        assertEquals(1, lib.getEventListeners().size());
    }

    @Test
    public void testEventListenersRemoveClear() {
        MockLSDEventListener listener0 = new MockLSDEventListener();
        MockLSDEventListener listener1 = new MockLSDEventListener();
        MockLSDEventListener listener2 = new MockLSDEventListener();
        MockLSDEventListener listener3 = new MockLSDEventListener();
        lib.addEventListener(listener0).addEventListener(listener1).addEventListener(listener2).addEventListener(listener3);
        assertEquals(4, lib.getEventListeners().size());

        lib.removeEventListener(listener0);
        assertEquals(3, lib.getEventListeners().size());

        lib.removeEventListener(listener0);
        assertEquals(3, lib.getEventListeners().size());
        lib.removeEventListener(new MockLSDEventListener());
        assertEquals(3, lib.getEventListeners().size());
        lib.removeEventListener(null);
        assertEquals(3, lib.getEventListeners().size());

        lib.removeEventListener(listener1).removeEventListener(listener2);
        assertEquals(1, lib.getEventListeners().size());
        assertTrue(lib.getEventListeners().contains(listener3));

        lib.clearEventListeners();
        assertEquals(0, lib.getEventListeners().size());
    }

    @Test
    public void testEventListenersRemoveInvalid() {
        MockLSDEventListener listener = new MockLSDEventListener();
        assertFalse(lib.getEventListeners().contains(listener));
        lib.removeEventListener(listener); // allowed no-op

        lib.removeEventListener(null); // also allowed
    }

    @Test
    public void testEventListenersFire() {
        MockLSDEventListener counter = new MockLSDEventListener();
        counter.assertCountsEqual(0, 0, 0);

        // ensure listener doesn't receive events if not registered
        LSDController.getInstance().respawn(new Vector3(0,0,0), true, true);
        LSDController.getInstance().gameTick(new Vector3(0,0,0));
        LSDController.getInstance().render(new Vector3(0,0,0), false);
        counter.assertCountsEqual(0, 0, 0);

        // register listener and ensure events are received
        lib.addEventListener(counter);

        LSDController.getInstance().respawn(new Vector3(0,0,0), true, true);
        counter.assertCountsEqual(1, 0, 0);
        LSDController.getInstance().gameTick(new Vector3(0,0,0));
        counter.assertCountsEqual(1, 1, 0);
        LSDController.getInstance().render(new Vector3(0,0,0), false);
        counter.assertCountsEqual(1, 1, 1);
    }

    @Test
    public void testEventListenersFireMultiple() {
        MockLSDEventListener counter0 = new MockLSDEventListener();
        MockLSDEventListener counter1 = new MockLSDEventListener();
        MockLSDEventListener counter2 = new MockLSDEventListener();
        lib.addEventListener(counter0).addEventListener(counter1).addEventListener(counter2);
        LSDController.getInstance().respawn(new Vector3(0,0,0), true, true);
        counter0.assertCountsEqual(1, 0, 0);
        counter1.assertCountsEqual(1, 0, 0);
        counter2.assertCountsEqual(1, 0, 0);
        LSDController.getInstance().gameTick(new Vector3(0,0,0));
        counter0.assertCountsEqual(1, 1, 0);
        counter1.assertCountsEqual(1, 1, 0);
        counter2.assertCountsEqual(1, 1, 0);
        LSDController.getInstance().render(new Vector3(0,0,0), false);
        counter0.assertCountsEqual(1, 1, 1);
        counter1.assertCountsEqual(1, 1, 1);
        counter2.assertCountsEqual(1, 1, 1);
    }

    // ----------------------------------------------------------------------
    // Other properties
    // ----------------------------------------------------------------------

    @Test
    public void testInstanceIdUsedInToString() {
        assertFalse(lib.getInstanceId().isEmpty());
        assertFalse(lib.toString().equals(lib.getInstanceId()));
        assertTrue(lib.toString().startsWith(lib.getInstanceId()));
    }

    @Test
    public void testInstanceIdUnique() {
        LibShapeDraw lib2 = new LibShapeDraw();
        assertFalse(lib.getInstanceId().equals(lib2.getInstanceId()));
    }

    @Test
    public void testVisibleDefault() {
        assertTrue(lib.isVisible());
    }

    @Test
    public void testVisibleWhenHidingGuiDefault() {
        assertFalse(lib.isVisibleWhenHidingGui());
    }

    @Test
    public void testVisibleRender() {
        MockShape counter = new MockShape();
        lib.addShape(counter);

        lib.setVisible(false);
        lib.setVisibleWhenHidingGui(false);
        renderCheck(false, counter, false);
        renderCheck(false, counter, true);

        lib.setVisible(true);
        lib.setVisibleWhenHidingGui(false);
        renderCheck(true, counter, false);
        renderCheck(false, counter, true);

        lib.setVisible(false);
        lib.setVisibleWhenHidingGui(true);
        renderCheck(false, counter, false);
        renderCheck(false, counter, true);

        lib.setVisible(true);
        lib.setVisibleWhenHidingGui(true);
        renderCheck(true, counter, false);
        renderCheck(true, counter, true);
    }
}
