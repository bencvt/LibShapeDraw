package libshapedraw.shape;

import static org.junit.Assert.*;
import libshapedraw.MockMinecraftAccess;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.Vector3;

import org.junit.Test;

public class TestWireframeLine extends SetupTestEnvironment.TestCase {
    @Test
    public void testConstructors() {
        new WireframeLine(1.0,2.0,3.0, 4.0,5.0,6.0);
        new WireframeLine(new Vector3(1.0,2.0,3.0), new Vector3(4.0,5.0,6.0));

        // it is valid to have both points be the same values
        new WireframeLine(0,0,0, 0,0,0);
        // or even the same instance
        Vector3 v = new Vector3(8.67, -5.3, 0.9);
        WireframeLine shape = new WireframeLine(v, v);
        assertSame(shape.getPointA(), shape.getPointB());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidNullA() {
        new WireframeLine(null, new Vector3(1.0, 2.0, 3.0));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidNullB() {
        new WireframeLine(new Vector3(1.0, 2.0, 3.0), null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidNullAB() {
        new WireframeLine(null, null);
    }

    @Test
    public void testGetSet() {
        WireframeLine shape = new WireframeLine(1.0,2.0,3.0, 4.0,5.0,6.0);
        assertEquals(1.0, shape.getPointA().getX(), 0.0);
        assertEquals(1.0, shape.getPointB().getX(), 4.0);
        assertEquals("(1.0,2.0,3.0)", shape.getPointA().toString());
        assertEquals("(4.0,5.0,6.0)", shape.getPointB().toString());
        assertNotSame(shape.getPointA(), shape.getPointB());

        shape.setPointA(shape.getPointB());
        assertSame(shape.getPointA(), shape.getPointB());
        shape.setPointB(new Vector3(-1.0, -2.0, -3.0));
        assertNotSame(shape.getPointA(), shape.getPointB());
        assertEquals("(-1.0,-2.0,-3.0)", shape.getPointB().toString());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetInvalidNullA() {
        new WireframeLine(1.0,2.0,3.0, 4.0,5.0,6.0).setPointA(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetInvalidNullB() {
        new WireframeLine(1.0,2.0,3.0, 4.0,5.0,6.0).setPointB(null);
    }

    @Test
    public void testLineStyle() {
        WireframeLine shape = new WireframeLine(Vector3.ZEROS.copy(), Vector3.ZEROS.copy());
        assertNull(shape.getLineStyle());
        assertSame(LineStyle.DEFAULT, shape.getEffectiveLineStyle());

        shape.setLineStyle(Color.BISQUE.copy(), 5.0F, true);
        assertNotNull(shape.getLineStyle());
        assertEquals("(0xffe4c4ff,5.0|0xffe4c43f,5.0)", shape.getLineStyle().toString());
        assertSame(shape.getLineStyle(), shape.getEffectiveLineStyle());
    }

    @Test
    public void testGetOrigin() {
        assertEquals("(1.0,2.0,3.0)", new WireframeLine(1.0,2.0,3.0, 4.0,5.0,6.0).getOriginReadonly().toString());
    }

    @Test
    public void testRender() {
        MockMinecraftAccess mc = new MockMinecraftAccess();            
        for (boolean twice : new boolean[] {true, false}) {
            WireframeLine shape = new WireframeLine(1.0,2.0,3.0, 4.0,5.0,6.0);
            shape.setLineStyle(Color.WHITE.copy(), 1.0F, false);
            shape.setLineStyle(Color.RED.copy(), 2.0F, twice);
            assertEquals(twice, shape.isVisibleThroughTerrain());

            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 2, twice);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 8, twice);

            // revert to the default line style
            shape.setLineStyle(null);
            assertSame(LineStyle.DEFAULT, shape.getEffectiveLineStyle());
            assertTrue(shape.isVisibleThroughTerrain());
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 2, true);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 8, true);
        }
    }
}
