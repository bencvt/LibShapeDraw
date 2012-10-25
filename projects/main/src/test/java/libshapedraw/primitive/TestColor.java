package libshapedraw.primitive;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;

import org.junit.Test;

public class TestColor extends SetupTestEnvironment.TestCase {
    @Test
    public void testConstructors() {
        assertEquals(0x3fbf7ff2, new Color(0.25, 0.75, 0.5, 0.95).getRGBA());
        assertEquals(0x3fbf7fff, new Color(0.25, 0.75, 0.5).getRGBA());
        assertEquals(0xff00ffff, new Color(Color.MAGENTA).getRGBA());
        assertEquals(0xdeadbeef, new Color(0xdeadbeef).getRGBA());
        assertEquals(0xffffff85, new Color(-123).getRGBA());
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNull() {
        new Color(null);
    }

    @Test
    public void testConstructorClampBounds() {
        assertEquals("0x00ffffff", new Color(-123.456, 1.0, 1.0).toString());
        assertEquals("0xff003fff", new Color(999.9, -4.2, 0.25, 67.8).toString());
    }

    @Test
    public void testARGB() {
        assertEquals(0xefdeadbe, new Color(0xdeadbeef).getARGB());
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
        assertEquals("0x60606060", c.toString());
        c.setRed(0.25F);
        assertEquals("0x3f606060", c.toString());
        c.setGreen(0.75F);
        assertEquals("0x3fbf6060", c.toString());
        c.setBlue(0.5F);
        assertEquals("0x3fbf7f60", c.toString());
        c.setAlpha(0.95F);
        assertEquals("0x3fbf7ff2", c.toString());
        c.setRGBA(0x12345678);
        assertEquals("0x12345678", c.toString());
        c.set(Color.RED);
        assertEquals("0xff0000ff", c.toString());
    }

    @Test
    public void testModifyClampValue() {
        assertEquals("0xdead00ef", new Color(0xdeadbeef).setBlue(-0.5F).toString());
        assertEquals("0xdeadffef", new Color(0xdeadbeef).setBlue(123.456F).toString());
    }

    @Test
    public void testScaleAlpha() {
        Color c = Color.BEIGE.copy();
        c.setAlpha(0.5).scaleAlpha(1.0);
        assertEquals("0xf5f5dc7f", c.toString());
        c.setAlpha(0.5).scaleAlpha(0.0);
        assertEquals("0xf5f5dc00", c.toString());
        c.setAlpha(0.5).scaleAlpha(-1.0);
        assertEquals("0xf5f5dc00", c.toString());
        c.setAlpha(0.5).scaleAlpha(-343.25);
        assertEquals("0xf5f5dc00", c.toString());
        c.setAlpha(0.5).scaleAlpha(0.25);
        assertEquals("0xf5f5dc1f", c.toString());
        c.setAlpha(0.5).scaleAlpha(1.0078125);
        assertEquals("0xf5f5dc80", c.toString());
        c.setAlpha(0.5).scaleAlpha(1.5);
        assertEquals("0xf5f5dcbf", c.toString());
        c.setAlpha(0.5).scaleAlpha(343.25);
        assertEquals("0xf5f5dcff", c.toString());
    }

    @Test
    public void testScaleRGB() {
        Color c;
        c = Color.BEIGE.copy().scaleRGB(1.0);
        assertEquals("0xf5f5dcff", c.toString());
        c = Color.BEIGE.copy().scaleRGB(0.0);
        assertEquals("0x000000ff", c.toString());
        c = Color.BEIGE.copy().scaleRGB(-1.0);
        assertEquals("0x000000ff", c.toString());
        c = Color.BEIGE.copy().scaleRGB(-343.25);
        assertEquals("0x000000ff", c.toString());
        c = Color.BEIGE.copy().scaleRGB(0.25);
        assertEquals("0x3d3d37ff", c.toString());
        c = Color.BEIGE.copy().scaleRGB(1.0078125);
        assertEquals("0xf6f6ddff", c.toString());
        c = Color.BEIGE.copy().scaleRGB(1.5);
        assertEquals("0xffffffff", c.toString());
        c = Color.BEIGE.copy().scaleRGB(343.25);
        assertEquals("0xffffffff", c.toString());
    }

    @Test
    public void testBlend() {
        ReadonlyColor fromColor = Color.CRIMSON;
        assertEquals("0xdc143cff", fromColor.toString());
        ReadonlyColor toColor = Color.MEDIUM_BLUE.copy().setAlpha(0.25);
        assertEquals("0x0000cd3f", toColor.toString());
        Color c;
        c = fromColor.copy().blend(toColor, 0.0);
        assertEquals("0xdc143cff", c.toString());
        c = fromColor.copy().blend(toColor, 0.25);
        assertEquals("0xa50f60cf", c.toString());
        c = fromColor.copy().blend(toColor, 0.5);
        assertEquals("0x6e0a849f", c.toString());
        c = fromColor.copy().blend(toColor, 0.75);
        assertEquals("0x3705a86f", c.toString());
        c = fromColor.copy().blend(toColor, 1.0);
        assertEquals("0x0000cd3f", c.toString());

        // percentage out of normal bounds is fine
        assertEquals("0xff5200ff", fromColor.copy().blend(toColor, -3.14).toString());
        assertEquals("0xffff00ff", fromColor.copy().blend(toColor, -999.99).toString());
        assertEquals("0x0000f806", fromColor.copy().blend(toColor, 1.3).toString());
        assertEquals("0x0000ff00", fromColor.copy().blend(toColor, 867.5309).toString());
    }

    @Test(expected=NullPointerException.class)
    public void testBlendInvalidNull() {
        Color.CRIMSON.copy().blend(null, 0.5);
    }

    @Test
    public void testSetRandom() {
        // no exceptions thrown
        Color.WHITE.copy().setRandom();

        // setRandomRGB doesn't touch alpha
        assertTrue(1.0 == Color.WHITE.copy().setRandomRGB().getAlpha());
    }

    @Test
    public void testGetNamedColor() {
        assertSame(Color.RED, Color.getNamedColor("RED"));
        assertSame(Color.RED, Color.getNamedColor("red"));
        assertSame(Color.RED, Color.getNamedColor("Red"));
        assertSame(Color.RED, Color.getNamedColor(" r e D "));
        assertSame(Color.RED, Color.getNamedColor("R_ED"));

        assertSame(Color.PAPAYA_WHIP, Color.getNamedColor("papayawhip"));
        assertSame(Color.PAPAYA_WHIP, Color.getNamedColor("Papayawhip"));
        assertSame(Color.PAPAYA_WHIP, Color.getNamedColor("PapayaWhip"));
        assertSame(Color.PAPAYA_WHIP, Color.getNamedColor("Papaya Whip"));
        assertSame(Color.PAPAYA_WHIP, Color.getNamedColor("Papaya_Whip"));
        assertSame(Color.PAPAYA_WHIP, Color.getNamedColor("PAPAYA_WHIP"));
        assertSame(Color.PAPAYA_WHIP, Color.getNamedColor("PAPAYA____WHIP"));

        assertNull(Color.getNamedColor(null));
        assertNull(Color.getNamedColor(""));
        assertNull(Color.getNamedColor(" "));
        assertNull(Color.getNamedColor("\t"));
        assertNull(Color.getNamedColor("NOT_A_COLOR"));
        assertNull(Color.getNamedColor("bloo"));
        assertNull(Color.getNamedColor("Papaya"));
        assertNull(Color.getNamedColor("PapayaWhipx"));

        assertSame(Color.TRANSPARENT_BLACK, Color.getNamedColor("transparent black"));
        assertSame(Color.TRANSPARENT_WHITE, Color.getNamedColor("transparent white"));
        assertNull(Color.getNamedColor("transparent"));
    }
}
