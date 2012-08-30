package libshapedraw.primitive;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;

import org.junit.Test;

public class TestLineStyle extends SetupTestEnvironment {
    @Test
    public void testDefault() {
        assertNotNull(LineStyle.DEFAULT);
        assertEquals("(0xff00ffcc,3.0|0xff00ff33,3.0)", LineStyle.DEFAULT.toString());
    }

    @Test
    public void testConstructor() {
        assertEquals("(0xffffffff,2.5)",
                new LineStyle(Color.WHITE.copy(), 2.5F, false).toString());
        assertEquals("(0xff00007f,12.0)",
                new LineStyle(Color.RED.copy().setAlpha(0.5), 12.0F, false).toString());
        assertEquals("(0xff00007f,12.0|0xff00001f,12.0)",
                new LineStyle(Color.RED.copy().setAlpha(0.5), 12.0F, true).toString());
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidColor() {
        new LineStyle(null, 1.0F, false);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidWidth() {
        new LineStyle(Color.WHITE.copy(), -32.5F, false);
    }

    @Test
    public void testConstructorCopy() {
        ReadonlyLineStyle s0 = new LineStyle(Color.RED.copy().setAlpha(0.5), 12.0F, true);

        ReadonlyLineStyle s1 = new LineStyle(s0);
        assertNotSame(s0, s1);
        assertEquals(s0.toString(), s1.toString());

        LineStyle s2 = new LineStyle(s0);
        assertNotSame(s0, s2);
        assertEquals(s0.toString(), s2.toString());
        s2.setMainColor(Color.GREEN.copy());
        assertFalse(s0.toString().equals(s2.toString()));
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorCopyInvalidNull() {
        new LineStyle(null);
    }

    @Test
    public void testCopy() {
        ReadonlyLineStyle s0 = new LineStyle(Color.RED.copy().setAlpha(0.5), 12.0F, true);

        ReadonlyLineStyle s1 = s0.copy();
        assertNotSame(s0, s1);
        assertEquals(s0.toString(), s1.toString());

        LineStyle s2 = s0.copy();
        assertNotSame(s0, s2);
        assertEquals(s0.toString(), s2.toString());
        s2.setMainColor(Color.GREEN.copy());
        assertFalse(s0.toString().equals(s2.toString()));
    }

    @Test
    public void testDeepCopy() {
        ReadonlyLineStyle orig = new LineStyle(Color.RED.copy().setAlpha(0.5), 12.0F, true);

        // shallow copy:
        ReadonlyLineStyle shallow = orig.copy();
        // same values
        assertEquals(orig.getMainColor().toString(), shallow.getMainColor().toString());
        assertEquals(orig.getSecondaryColor().toString(), shallow.getSecondaryColor().toString());
        // same instances
        assertSame(orig.getMainColor(), shallow.getMainColor());
        assertSame(orig.getSecondaryColor(), shallow.getSecondaryColor());

        // deep copy:
        ReadonlyLineStyle deep = orig.deepCopy();
        // same values
        assertEquals(orig.getMainColor().toString(), deep.getMainColor().toString());
        assertEquals(orig.getSecondaryColor().toString(), deep.getSecondaryColor().toString());
        // different instances
        assertNotSame(orig.getMainColor(), deep.getMainColor());
        assertNotSame(orig.getSecondaryColor(), deep.getSecondaryColor());

        assertNotSame(shallow.getMainColor(), deep.getMainColor());
        assertNotSame(shallow.getSecondaryColor(), deep.getSecondaryColor());
    }

    @Test
    public void testSetColors() {
        LineStyle s = new LineStyle(Color.BLUE.copy(), 5.0F, false);
        Color c = Color.BEIGE.copy();
        s.setMainColor(c);
        assertSame(c, s.getMainColor());
        s.setSecondaryColor(c);
        assertSame(c, s.getSecondaryColor());
        assertTrue(s.hasSecondaryColor());
        s.setSecondaryColor(null);
        assertNull(s.getSecondaryColor());
        assertFalse(s.hasSecondaryColor());
    }

    @Test(expected=NullPointerException.class)
    public void testSetMainColorInvalidNull() {
        LineStyle s = new LineStyle(Color.BLUE.copy(), 5.0F, false);
        s.setMainColor(null);
    }

    @Test
    public void testSetWidths() {
        LineStyle s = new LineStyle(Color.BLUE.copy(), 5.0F, false);
        assertEquals(5.0F, s.getMainWidth(), 0.0F);
        assertEquals(5.0F, s.getSecondaryWidth(), 0.0F);
        s.setMainWidth(3.14F);
        assertEquals(3.14F, s.getMainWidth(), 0.0F);
        assertEquals(5.0F, s.getSecondaryWidth(), 0.0F);
        s.setSecondaryWidth(0.0F);
        assertEquals(3.14F, s.getMainWidth(), 0.0F);
        assertEquals(0.0F, s.getSecondaryWidth(), 0.0F);
        // Even though there isn't a secondary color to go with it,
        // it is not an error to set a secondary width.
        assertFalse(s.hasSecondaryColor());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetMainWidthInvalid() {
        LineStyle s = new LineStyle(Color.BLUE.copy(), 5.0F, false);
        s.setMainWidth(-4.3F);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetSecondaryWidthInvalid() {
        LineStyle s = new LineStyle(Color.BLUE.copy(), 5.0F, false);
        s.setSecondaryWidth(-4.3F);
    }

    @Test
    public void testSet() {
        LineStyle s = new LineStyle(Color.WHITE.copy(), 2.5F, false);
        assertEquals("(0xffffffff,2.5)", s.toString());

        s.set(Color.RED.copy().setAlpha(0.5), 12.0F, false);
        assertEquals("(0xff00007f,12.0)", s.toString());

        s.set(Color.RED.copy().setAlpha(0.5), 12.0F, true);
        assertEquals("(0xff00007f,12.0|0xff00001f,12.0)", s.toString());
    }
}
