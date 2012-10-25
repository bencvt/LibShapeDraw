package libshapedraw.shape;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import libshapedraw.MockMinecraftAccess;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.transform.ShapeRotate;
import libshapedraw.transform.ShapeScale;
import libshapedraw.transform.ShapeTranslate;

import org.junit.Test;

public class TestShape extends SetupTestEnvironment.TestCase {
    @Test
    public void testVisible() {
        MockShape shape = new MockShape();
        MockMinecraftAccess mc = new MockMinecraftAccess();

        assertTrue(shape.isVisible());
        shape.render(mc);
        assertEquals(1, shape.getCountRender());

        shape.setVisible(false);
        assertFalse(shape.isVisible());
        shape.render(mc);
        assertEquals(1, shape.getCountRender());

        shape.setVisible(true);
        assertTrue(shape.isVisible());
        shape.render(mc);
        assertEquals(2, shape.getCountRender());
    }

    @Test
    public void testTransforms() {
        MockShape shape = new MockShape();
        assertEquals(0, shape.getTransforms().size());

        MockMinecraftAccess mc = new MockMinecraftAccess();
        shape.render(mc);
        assertEquals(1, shape.getCountRender());

        shape.getTransforms().add(new ShapeRotate(45.0, 0.0, 1.0, 0.0));
        assertEquals(1, shape.getTransforms().size());
        shape.addTransform(new ShapeScale(1.0, 1.5, 1.0));
        assertEquals(2, shape.getTransforms().size());
        ShapeTranslate transform0 = new ShapeTranslate(0.0, 0.0, 18.25);
        ShapeTranslate transform1 = new ShapeTranslate(0.0, -5.0, 0.0);
        shape.addTransform(transform0).addTransform(transform1);
        assertEquals(4, shape.getTransforms().size());

        shape.render(mc);
        assertEquals(2, shape.getCountRender());

        shape.removeTransform(transform0).removeTransform(transform1);
        assertEquals(2, shape.getTransforms().size());
        shape.removeTransform(new ShapeTranslate(1.0, 2.0, 3.0));
        assertEquals(2, shape.getTransforms().size());
        shape.removeTransform(null);
        assertEquals(2, shape.getTransforms().size());

        shape.render(mc);
        assertEquals(3, shape.getCountRender());
    }

    @Test
    public void testToString() {
        MockShape shape = new MockShape();
        final String prefix = "MockShape@" + Integer.toHexString(shape.hashCode());
        assertEquals(prefix+"{V}", shape.toString());
        shape.setVisible(false);
        assertEquals(prefix+"{}", shape.toString());
        shape.addTransform(new ShapeScale(1.0, 1.5, 1.0));
        assertEquals(prefix+"{T}", shape.toString());
        shape.addTransform(new ShapeTranslate(0.0, 0.0, 18.25));
        assertEquals(prefix+"{TT}", shape.toString());
        shape.setVisible(true);
        assertEquals(prefix+"{VTT}", shape.toString());
        shape.getTransforms().clear();
        assertEquals(prefix+"{V}", shape.toString());
    }
}
