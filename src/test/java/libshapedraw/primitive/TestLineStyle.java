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

        assertEquals("(0x0000ffff,2.5)",
                new LineStyle(Color.BLUE.copy(), 2.5F, null, 0.0F).toString());
        assertEquals("(0x0000ffff,7.0|0xff0000ff,3.5)",
                new LineStyle(Color.BLUE.copy(), 7.0F, Color.RED.copy(), 3.5F).toString());

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
        ReadonlyLineStyle orig = new LineStyle(Color.RED.copy().setAlpha(0.5), 12.0F, true);

        ReadonlyLineStyle copy0 = new LineStyle(orig);
        assertNotSame(orig, copy0);
        assertEquals(orig.toString(), copy0.toString());
        assertNotSame(orig.getMainReadonlyColor(), copy0.getMainReadonlyColor());
        assertEquals(orig.getMainReadonlyColor().toString(), copy0.getMainReadonlyColor().toString());
        assertNotSame(orig.getSecondaryReadonlyColor(), copy0.getSecondaryReadonlyColor());
        assertEquals(orig.getSecondaryReadonlyColor().toString(), copy0.getSecondaryReadonlyColor().toString());

        LineStyle copy1 = new LineStyle(orig);
        assertNotSame(orig, copy1);
        assertEquals(orig.toString(), copy1.toString());
        copy1.setMainColor(Color.GREEN.copy());
        assertFalse(orig.toString().equals(copy1.toString()));
    }

    @Test
    public void testConstructorCopyNullSecondaryColor() {
        ReadonlyLineStyle orig = new LineStyle(Color.YELLOW.copy().setAlpha(0.5), 12.0F, false);
        assertNull(orig.getSecondaryReadonlyColor());

        ReadonlyLineStyle copy = new LineStyle(orig);
        assertNotSame(orig, copy);
        assertEquals(orig.toString(), copy.toString());
        assertNotSame(orig.getMainReadonlyColor(), copy.getMainReadonlyColor());
        assertEquals(orig.getMainReadonlyColor().toString(), copy.getMainReadonlyColor().toString());
        assertNull(copy.getSecondaryReadonlyColor());
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorCopyInvalidNull() {
        new LineStyle(null);
    }

    @Test
    public void testCopy() {
        ReadonlyLineStyle orig = new LineStyle(Color.RED.copy().setAlpha(0.5), 12.0F, true);

        ReadonlyLineStyle copy0 = orig.copy();
        assertNotSame(orig, copy0);
        assertEquals(orig.toString(), copy0.toString());
        assertNotSame(orig.getMainReadonlyColor(), copy0.getMainReadonlyColor());
        assertEquals(orig.getMainReadonlyColor().toString(), copy0.getMainReadonlyColor().toString());
        assertNotSame(orig.getSecondaryReadonlyColor(), copy0.getSecondaryReadonlyColor());
        assertEquals(orig.getSecondaryReadonlyColor().toString(), copy0.getSecondaryReadonlyColor().toString());

        LineStyle copy1 = orig.copy();
        assertNotSame(orig, copy1);
        assertEquals(orig.toString(), copy1.toString());
        copy1.setMainColor(Color.GREEN.copy());
        assertFalse(orig.toString().equals(copy1.toString()));
    }

    @Test
    public void testCopyNullSecondaryColor() {
        ReadonlyLineStyle orig = new LineStyle(Color.YELLOW.copy().setAlpha(0.5), 12.0F, false);
        assertNull(orig.getSecondaryReadonlyColor());

        ReadonlyLineStyle copy = orig.copy();
        assertNotSame(orig, copy);
        assertEquals(orig.toString(), copy.toString());
        assertNotSame(orig.getMainReadonlyColor(), copy.getMainReadonlyColor());
        assertEquals(orig.getMainReadonlyColor().toString(), copy.getMainReadonlyColor().toString());
        assertNull(copy.getSecondaryReadonlyColor());
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
