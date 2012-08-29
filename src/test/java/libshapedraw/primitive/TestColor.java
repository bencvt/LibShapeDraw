package libshapedraw.primitive;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.primitive.Color;

import org.junit.Test;

public class TestColor extends SetupTestEnvironment.TestCase {
    @Test
    public void testConstructors() {
        assertEquals(0x3fbf7ff2, new Color(0.25F, 0.75F, 0.5F, 0.95F).getRGBA());
        assertEquals(0x3fbf7fff, new Color(0.25F, 0.75F, 0.5F).getRGBA());
        assertEquals(0xff00ffff, new Color(Color.MAGENTA).getRGBA());
        assertEquals(0xdeadbeef, new Color(0xdeadbeef).getRGBA());
        assertEquals(0xffffff85, new Color(-123).getRGBA());
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNull() {
        new Color(null);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testConstructorInvalidBounds() {
        new Color(-123.456F, 1.0F, 1.0F);
    }

    @Test
    public void testToString() {
        assertEquals("0xdeadbeef", new Color(0xdeadbeef).toString());
        assertEquals("0x00000000", new Color(0).toString());
        assertEquals("0xffffff85", new Color(-123).toString());
    }

    @Test
    public void testModify() {
        Color c = new Color(0x60606060);
        assertEquals(0x60606060, c.getRGBA());
        c.setRed(0.25F);
        assertEquals(0x3f606060, c.getRGBA());
        c.setGreen(0.75F);
        assertEquals(0x3fbf6060, c.getRGBA());
        c.setBlue(0.5F);
        assertEquals(0x3fbf7f60, c.getRGBA());
        c.setAlpha(0.95F);
        assertEquals(0x3fbf7ff2, c.getRGBA());
        c.setRGBA(0x12345678);
        assertEquals(0x12345678, c.getRGBA());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testModifyInvalidValueLow() {
        new Color(0xdeadbeef).setBlue(-0.5F);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testModifyInvalidValueHigh() {
        new Color(0xdeadbeef).setBlue(123.456F);
    }
}
