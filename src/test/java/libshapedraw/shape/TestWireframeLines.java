package libshapedraw.shape;

import static org.junit.Assert.*;

import java.util.ArrayList;

import libshapedraw.MockMinecraftAccess;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.primitive.Color;
import libshapedraw.primitive.LineStyle;
import libshapedraw.primitive.ReadonlyVector3;
import libshapedraw.primitive.Vector3;

import org.junit.Test;

public class TestWireframeLines extends SetupTestEnvironment.TestCase {
    @Test
    public void testConstructor() {
        ArrayList<ReadonlyVector3> arr;

        arr = new ArrayList<ReadonlyVector3>();
        new WireframeLines(arr);

        arr = new ArrayList<ReadonlyVector3>();
        arr.add(new Vector3(1.0, 2.0, 3.0));
        new WireframeLines(arr);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNull() {
        new WireframeLines(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidNullItem() {
        ArrayList<ReadonlyVector3> arr = new ArrayList<ReadonlyVector3>();
        arr.add(null);
        new WireframeLines(arr);
    }

    @Test
    public void testConstructorHiddenNullItem() {
        ArrayList<ReadonlyVector3> arr = new ArrayList<ReadonlyVector3>();
        arr.add(new Vector3(1.0, 2.0, 3.0));
        // this null is invalid and will break things later, but the constructor will accept it
        arr.add(null);
        new WireframeLines(arr);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testConstructorTypeErased() {
        ArrayList arr;

        arr = new ArrayList();
        new WireframeLines(arr);

        arr = new ArrayList();
        arr.add(new Vector3(1.0, 2.0, 3.0));
        new WireframeLines(arr);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorTypeErasedInvalidWrongType() {
        ArrayList arr = new ArrayList();
        arr.add(new Object());
        new WireframeLines(arr);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testConstructorTypeErasedHiddenWrongType() {
        ArrayList arr = new ArrayList();
        arr.add(new Vector3(1.0, 2.0, 3.0));
        // this is invalid and will break things later, but the constructor will accept it
        arr.add(new Object());
        new WireframeLines(arr);
    }

    @Test
    public void testGetSetPoints() {
        // setPoint is called from the constructor so most of its logic has already been tested above

        ArrayList<ReadonlyVector3> arr0 = new ArrayList<ReadonlyVector3>();
        arr0.add(new Vector3(1.0, 2.0, 3.0));
        arr0.add(new Vector3(4.0, 5.0, 6.0));
        assertEquals(2, arr0.size());
        assertEquals("[(1.0,2.0,3.0), (4.0,5.0,6.0)]", arr0.toString());

        ArrayList<ReadonlyVector3> arr1 = new ArrayList<ReadonlyVector3>();
        arr1.add(new Vector3(-1.0, -2.0, -3.0));
        arr1.add(new Vector3(-4.0, -5.0, -6.0));
        arr1.add(new Vector3(-7.0, -8.0, -9.0));
        assertEquals(3, arr1.size());
        assertEquals("[(-1.0,-2.0,-3.0), (-4.0,-5.0,-6.0), (-7.0,-8.0,-9.0)]", arr1.toString());

        assertNotSame(arr0, arr1);

        WireframeLines shape = new WireframeLines(arr0);
        assertSame(arr0, shape.getPoints());
        assertNotSame(arr1, shape.getPoints());
        assertEquals("[(1.0,2.0,3.0), (4.0,5.0,6.0)]", shape.getPoints().toString());

        shape.setPoints(arr1);
        assertNotSame(arr0, shape.getPoints());
        assertSame(arr1, shape.getPoints());
        assertEquals("[(-1.0,-2.0,-3.0), (-4.0,-5.0,-6.0), (-7.0,-8.0,-9.0)]", shape.getPoints().toString());
    }

    @Test
    public void testGetSetRenderCap() {
        WireframeLines shape = new WireframeLines(new ArrayList<ReadonlyVector3>());
        assertEquals(-1, shape.getRenderCap());
        shape.setRenderCap(0);
        assertEquals(0, shape.getRenderCap());
        shape.setRenderCap(50);
        assertEquals(50, shape.getRenderCap());
        shape.setRenderCap(-20);
        assertEquals(-20, shape.getRenderCap());
        shape.setRenderCap(Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, shape.getRenderCap());
        shape.setRenderCap(Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, shape.getRenderCap());
    }

    @Test
    public void testLineStyle() {
        WireframeLines shape = new WireframeLines(new ArrayList<ReadonlyVector3>());
        assertNull(shape.getLineStyle());
        assertSame(LineStyle.DEFAULT, shape.getEffectiveLineStyle());

        shape.setLineStyle(Color.BISQUE.copy(), 5.0F, true);
        assertNotNull(shape.getLineStyle());
        assertEquals("(0xffe4c4ff,5.0|0xffe4c43f,5.0)", shape.getLineStyle().toString());
        assertSame(shape.getLineStyle(), shape.getEffectiveLineStyle());
    }

    @Test
    public void testGetOrigin() {
        Vector3 buf = Vector3.ZEROS.copy();
        ArrayList<ReadonlyVector3> arr = new ArrayList<ReadonlyVector3>();
        WireframeLines shape = new WireframeLines(arr);

        // no points at all: 0,0,0
        shape.getOrigin(buf);
        assertEquals("(0.0,0.0,0.0)", buf.toString());

        // the first point is the origin
        arr.add(new Vector3(4.0, 5.5, -3.0));
        shape.getOrigin(buf);
        assertEquals("(4.0,5.5,-3.0)", buf.toString());

        // additional points are ignored
        arr.add(new Vector3(1.0, 2.0, 3.0));
        arr.add(new Vector3(7.0, -9.0, 213.5));
        shape.getOrigin(buf);
        assertEquals("(4.0,5.5,-3.0)", buf.toString());
    }

    @Test(expected=NullPointerException.class)
    public void testGetOriginInvalidNull() {
        new WireframeLines(new ArrayList<ReadonlyVector3>()).getOrigin(null);
    }

    @Test
    public void testRender() {
        Vector3 buf = Vector3.ZEROS.copy();
        MockMinecraftAccess mc = new MockMinecraftAccess();
        ArrayList<ReadonlyVector3> arr = new ArrayList<ReadonlyVector3>();
        for (boolean twice : new boolean[] {true, false}) {
            arr.clear();
            WireframeLines shape = new WireframeLines(arr);
            shape.setLineStyle(Color.WHITE.copy(), 1.0F, twice);
            assertEquals(twice, shape.isVisibleThroughTerrain());

            // No points == nothing to render
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(0, 0, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(0, 0, twice);
    
            // Only one point == nothing to render
            arr.add(new Vector3(0.0, 5.5, -12.5));
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(0, 0, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(0, 0, twice);
    
            // Two points make one line
            arr.add(new Vector3(7.0, 5.5, -12.5));
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(1, 2, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(4, 8, twice);
    
            // Three points make two lines
            arr.add(new Vector3(7.0, 15.5, -12.5));
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(2, 4, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(8, 16, twice);
    
            // Eleven points make ten lines
            arr.add(new Vector3(7.0, 15.5, -6.5));
            arr.add(new Vector3(7.0, 12.5, -3.5));
            arr.add(new Vector3(17.0, 12.5, -3.5));
            arr.add(new Vector3(17.0, 6.5, -3.5));
            arr.add(new Vector3(12.0, 7.5, -3.5));
            arr.add(new Vector3(10.0, 7.5, 3.5));
            arr.add(new Vector3(10.0, 7.5, 6.0));
            arr.add(new Vector3(20.0, 17.5, 6.0));
            assertEquals(11, arr.size());
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(10, 20, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(40, 80, twice);

            // Add a render cap, we only render that many lines
            shape.setRenderCap(5);
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(5, 10, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(20, 40, twice);

            // Remove the render cap, we render everything again
            shape.setRenderCap(-1);
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(10, 20, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(40, 80, twice);

            // A render cap that's larger than the number of line segments defined is fine too
            shape.setRenderCap(9001);
            mc.reset();
            shape.render(mc, buf);
            mc.assertCountsEqual(10, 20, twice);
            shape.render(mc, buf);
            shape.render(mc, buf);
            shape.render(mc, buf);
            mc.assertCountsEqual(40, 80, twice);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test(expected=ClassCastException.class)
    public void testRenderInvalidWrongType() {
        ArrayList arr = new ArrayList();
        arr.add(new Vector3(1.0, 2.0, 3.0));
        arr.add(new Vector3(4.0, 5.0, 6.0));
        arr.add("how did i get here");
        arr.add(new Vector3(7.0, 8.0, 9.0));
        WireframeLines shape = new WireframeLines(arr);
        shape.setLineStyle(Color.WHITE.copy(), 1.0F, true);
        shape.render(new MockMinecraftAccess(), Vector3.ZEROS.copy());
    }

    @Test(expected=NullPointerException.class)
    public void testRenderInvalidNull() {
        ArrayList<ReadonlyVector3> arr = new ArrayList<ReadonlyVector3>();
        arr.add(new Vector3(1.0, 2.0, 3.0));
        arr.add(new Vector3(4.0, 5.0, 6.0));
        arr.add(null);
        arr.add(new Vector3(7.0, 8.0, 9.0));
        WireframeLines shape = new WireframeLines(arr);
        shape.setLineStyle(Color.WHITE.copy(), 1.0F, true);
        shape.render(new MockMinecraftAccess(), Vector3.ZEROS.copy());
    }
}
