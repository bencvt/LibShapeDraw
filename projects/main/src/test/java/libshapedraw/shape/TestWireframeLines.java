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

    @Test(expected=IllegalArgumentException.class)
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
        ArrayList<ReadonlyVector3> arr = new ArrayList<ReadonlyVector3>();
        WireframeLines shape = new WireframeLines(arr);

        // no points at all: no origin
        assertNull(shape.getOriginReadonly());

        // the first point is the origin
        arr.add(new Vector3(4.0, 5.5, -3.0));
        assertEquals("(4.0,5.5,-3.0)", shape.getOriginReadonly().toString());

        // additional points are ignored
        arr.add(new Vector3(1.0, 2.0, 3.0));
        arr.add(new Vector3(7.0, -9.0, 213.5));
        assertEquals("(4.0,5.5,-3.0)", shape.getOriginReadonly().toString());
    }

    @Test
    public void testRender() {
        MockMinecraftAccess mc = new MockMinecraftAccess();
        ArrayList<ReadonlyVector3> arr = new ArrayList<ReadonlyVector3>();
        for (boolean seeThru : new boolean[] {true, false}) {
            arr.clear();
            WireframeLines shape = new WireframeLines(arr);
            shape.setLineStyle(Color.WHITE.copy(), 1.0F, seeThru);
            assertEquals(seeThru, shape.isVisibleThroughTerrain());

            // No points == nothing to render
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(0, 0, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(0, 0, seeThru);
    
            // Only one point makes no lines
            arr.add(new Vector3(0.0, 5.5, -12.5));
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 1, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 4, seeThru);
    
            // Two points make one line
            arr.add(new Vector3(7.0, 5.5, -12.5));
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 2, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 8, seeThru);
    
            // Three points make two lines
            arr.add(new Vector3(7.0, 15.5, -12.5));
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 3, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 12, seeThru);
    
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
            shape.render(mc);
            mc.assertCountsEqual(1, 11, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 44, seeThru);

            // Add a render cap, we only render that many lines
            shape.setRenderCap(5);
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 6, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 24, seeThru);

            // Remove the render cap, we render everything again
            shape.setRenderCap(-1);
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 11, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 44, seeThru);

            // A render cap that's larger than the number of line segments defined is fine too
            shape.setRenderCap(9001);
            mc.reset();
            shape.render(mc);
            mc.assertCountsEqual(1, 11, seeThru);
            shape.render(mc);
            shape.render(mc);
            shape.render(mc);
            mc.assertCountsEqual(4, 44, seeThru);
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
        shape.render(new MockMinecraftAccess());
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
        shape.render(new MockMinecraftAccess());
    }
}
