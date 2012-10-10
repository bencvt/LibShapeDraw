package libshapedraw;

import static org.junit.Assert.*;

import java.util.Collection;

import libshapedraw.LibShapeDraw;
import libshapedraw.event.MockLSDEventListener;
import libshapedraw.internal.Controller;
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
        Controller.getInstance().render(new Vector3(0,0,0), hideGui, true);
    }
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void addCollectionTypeErased(Collection c, Object o) {
        c.add(o);
    }

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

    @Test
    public void testIsControllerInitialized() {
        assertTrue(LibShapeDraw.isControllerInitialized());
    }

    // ----------------------------------------------------------------------
    // Shapes
    // ----------------------------------------------------------------------

    @Test
    public void testShapesAdd() {
        assertEquals(0, lib.getShapes().size());
        lib.getShapes().add(new MockShape());
        assertEquals(1, lib.getShapes().size());
        lib.addShape(new MockShape());
        assertEquals(2, lib.getShapes().size());
        lib.addShape(new MockShape()).addShape(new MockShape()); // method chaining
        assertEquals(4, lib.getShapes().size());
        render(false);
    }

    // This test is disabled because it would pass for Java 6 and earlier but fail for Java 7
    //@Test(expected=NullPointerException.class)
    //public void testShapesAddNull() {
    //    lib.addShape(null);
    //}

    @Test(expected=ClassCastException.class)
    public void testShapesAddInvalid() {
        addCollectionTypeErased(lib.getShapes(), new Object());
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
    public void testShapesRemove() {
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
    }

    // ----------------------------------------------------------------------
    // Event Listeners
    // ----------------------------------------------------------------------

    @Test
    public void testEventListenersAdd() {
        assertEquals(0, lib.getEventListeners().size());
        lib.getEventListeners().add(new MockLSDEventListener());
        assertEquals(1, lib.getEventListeners().size());
        lib.addEventListener(new MockLSDEventListener());
        assertEquals(2, lib.getEventListeners().size());
        lib.addEventListener(new MockLSDEventListener()).addEventListener(new MockLSDEventListener()); // method chaining
        assertEquals(4, lib.getEventListeners().size());
        render(false);
    }

    // This test is disabled because it would pass for Java 6 and earlier but fail for Java 7
    //@Test(expected=NullPointerException.class)
    //public void testEventListenersAddNull() {
    //    lib.addEventListener(null);
    //}

    @Test(expected=ClassCastException.class)
    public void testEventListenersAddInvalid() {
        addCollectionTypeErased(lib.getEventListeners(), new Object());
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
    public void testEventListenersRemove() {
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
    }

    @Test
    public void testEventListenersFire() {
        MockLSDEventListener counter = new MockLSDEventListener();
        counter.assertCountsEqual(0, 0, 0);

        // ensure listener doesn't receive events if not registered
        Controller.getInstance().respawn(new Vector3(0,0,0), true, true);
        Controller.getInstance().gameTick(new Vector3(0,0,0));
        Controller.getInstance().render(new Vector3(0,0,0), false, true);
        counter.assertCountsEqual(0, 0, 0);

        // register listener and ensure events are received
        lib.addEventListener(counter);

        Controller.getInstance().respawn(new Vector3(0,0,0), true, true);
        counter.assertCountsEqual(1, 0, 0);
        Controller.getInstance().gameTick(new Vector3(0,0,0));
        counter.assertCountsEqual(1, 1, 0);
        Controller.getInstance().render(new Vector3(0,0,0), false, true);
        counter.assertCountsEqual(1, 1, 1);
    }

    @Test
    public void testEventListenersFireMultiple() {
        MockLSDEventListener counter0 = new MockLSDEventListener();
        MockLSDEventListener counter1 = new MockLSDEventListener();
        MockLSDEventListener counter2 = new MockLSDEventListener();
        lib.addEventListener(counter0).addEventListener(counter1).addEventListener(counter2);
        Controller.getInstance().respawn(new Vector3(0,0,0), true, true);
        counter0.assertCountsEqual(1, 0, 0);
        counter1.assertCountsEqual(1, 0, 0);
        counter2.assertCountsEqual(1, 0, 0);
        Controller.getInstance().gameTick(new Vector3(0,0,0));
        counter0.assertCountsEqual(1, 1, 0);
        counter1.assertCountsEqual(1, 1, 0);
        counter2.assertCountsEqual(1, 1, 0);
        Controller.getInstance().render(new Vector3(0,0,0), false, true);
        counter0.assertCountsEqual(1, 1, 1);
        counter1.assertCountsEqual(1, 1, 1);
        counter2.assertCountsEqual(1, 1, 1);
    }

    // ----------------------------------------------------------------------
    // Other properties
    // ----------------------------------------------------------------------

    @Test
    public void testInstanceIdIsToString() {
        assertFalse(lib.getInstanceId().isEmpty());
        assertEquals(lib.getInstanceId(), lib.toString());
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
