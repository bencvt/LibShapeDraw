package libshapedraw.primitive;

import java.lang.reflect.Field;
import java.util.HashMap;

import libshapedraw.animation.Animates;
import libshapedraw.animation.trident.Timeline;
import libshapedraw.internal.LSDInternalReflectionException;

import org.lwjgl.opengl.GL11;

/**
 * Yet another class representing a Red/Green/Blue/Alpha color 4-tuple.
 * <p>
 * All modifiers support method chaining, e.g.
 * <code>Color result = Color.TOMATO.copy().setAlpha(0.4).scaleRGB(0.8).blend(Color.GREEN, 0.3);</code>
 */
public class Color implements ReadonlyColor, Animates<ReadonlyColor> {
    private static final long serialVersionUID = 1L;

    private double red;
    private double green;
    private double blue;
    private double alpha;

    /** @see Vector3#timeline */
    private transient Timeline timeline;

    public Color(double red, double green, double blue, double alpha) {
        set(red, green, blue, alpha);
    }

    public Color(double red, double green, double blue) {
        set(red, green, blue, 1.0);
    }

    /**
     * Construct this color using a 32-bit integer packed with each of the
     * color's components, one per byte. The order, from most significant to
     * least, is red, green, blue, alpha.
     * @see #convertARGBtoRGBA
     */
    public Color(int rgba) {
        setRGBA(rgba);
    }

    public Color(ReadonlyColor other) {
        set(other.getRed(), other.getGreen(), other.getBlue(), other.getAlpha());
    }

    @Override
    public Color copy() {
        return new Color(red, green, blue, alpha);
    }

    @Override
    public double getRed() {
        return red;
    }

    @Override
    public double getGreen() {
        return green;
    }

    @Override
    public double getBlue() {
        return blue;
    }

    @Override
    public double getAlpha() {
        return alpha;
    }

    @Override
    public int getRGBA() {
        return ((((int) (getRed() * 255.0)) & 0xff) << 24) |
                ((((int) (getGreen() * 255.0)) & 0xff) << 16) |
                ((((int) (getBlue() * 255.0)) & 0xff) << 8) |
                (((int) (getAlpha() * 255.0)) & 0xff);
    }

    @Override
    public int getARGB() {
        return ((((int) (getAlpha() * 255.0)) & 0xff) << 24) |
                ((((int) (getRed() * 255.0)) & 0xff) << 16) |
                ((((int) (getGreen() * 255.0)) & 0xff) << 8) |
                (((int) (getBlue() * 255.0)) & 0xff);
    }

    @Override
    public void glApply() {
        GL11.glColor4d(red, green, blue, alpha);
    }

    @Override
    public void glApply(double alphaScale) {
        GL11.glColor4d(red, green, blue, clamp(alpha * alphaScale));
    }

    /** @return true if two colors are equal, rounding each component. */
    @Override
    public boolean equals(Object other) {
        return other instanceof ReadonlyColor && hashCode() == other.hashCode();
    }

    @Override
    public int hashCode() {
        return getRGBA();
    }

    @Override
    public String toString() {
        return String.format("0x%08x", getRGBA());
    }

    // ========
    // Mutators
    // ========

    /**
     * Set this color's red component, clamped to [0.0, 1.0].
     * @return the same color object, modified in-place.
     */
    public Color setRed(double red) {
        this.red = clamp(red);
        return this;
    }

    /**
     * Set this color's green component, clamped to [0.0, 1.0].
     * @return the same color object, modified in-place.
     */
    public Color setGreen(double green) {
        this.green = clamp(green);
        return this;
    }

    /**
     * Set this color's blue component, clamped to [0.0, 1.0].
     * @return the same color object, modified in-place.
     */
    public Color setBlue(double blue) {
        this.blue = clamp(blue);
        return this;
    }

    /**
     * Set this color's alpha component, clamped to [0.0, 1.0].
     * @return the same color object, modified in-place.
     */
    public Color setAlpha(double alpha) {
        this.alpha = clamp(alpha);
        return this;
    }

    /**
     * Set all of this color's components, clamped to [0.0, 1.0].
     * @return the same color object, modified in-place.
     */
    public Color set(double red, double green, double blue, double alpha) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.alpha = clamp(alpha);
        return this;
    }

    /**
     * Set all of this color components to match another color's.
     * @return the same color object, modified in-place.
     */
    public Color set(ReadonlyColor other) {
        red = clamp(other.getRed());
        green = clamp(other.getGreen());
        blue = clamp(other.getBlue());
        alpha = clamp(other.getAlpha());
        return this;
    }

    /**
     * Multiply each of the red/green/blue color components by the given
     * factor, clamping the results to [0.0, 1.0].
     * @return the same color object, modified in-place.
     */
    public Color scaleRGB(double factor) {
        red = clamp(red*factor);
        green = clamp(green*factor);
        blue = clamp(blue*factor);
        return this;
    }

    /**
     * Multiply the alpha color component by the given factor, clamping the
     * result to [0.0, 1.0].
     * @return the same color object, modified in-place.
     */
    public Color scaleAlpha(double factor) {
        alpha = clamp(alpha*factor);
        return this;
    }

    /**
     * Blend all color components with another color's.
     * @return the same color object, modified in-place.
     */
    public Color blend(ReadonlyColor other, double percent) {
        setRed(blend(getRed(), other.getRed(), percent));
        setGreen(blend(getGreen(), other.getGreen(), percent));
        setBlue(blend(getBlue(), other.getBlue(), percent));
        setAlpha(blend(getAlpha(), other.getAlpha(), percent));
        return this;
    }

    /**
     * Set all color components from a packed 32-bit integer.
     * This is the reverse of {@link #getRGBA()}.
     * @return the same color object, modified in-place.
     */
    public Color setRGBA(int rgba) {
        setRed(((rgba & 0xff000000) >>> 24) / 255.0);
        setGreen(((rgba & 0xff0000) >>> 16) / 255.0);
        setBlue(((rgba & 0xff00) >>> 8) / 255.0);
        setAlpha((rgba & 0xff) / 255.0);
        return this;
    }

    /**
     * Set each of this color's components to a random value in [0.0, 1.0).
     * @return the same color object, modified in-place.
     */
    public Color setRandom() {
        red = Math.random();
        green = Math.random();
        blue = Math.random();
        alpha = Math.random();
        return this;
    }

    /**
     * Set this color's red, green, and blue (excluding alpha) components to a
     * random value in [0.0, 1.0).
     * @return the same color object, modified in-place.
     */
    public Color setRandomRGB() {
        red = Math.random();
        green = Math.random();
        blue = Math.random();
        return this;
    }

    private static double clamp(double x) {
        if (x < 0.0) {
            return 0.0;
        } else if (x > 1.0) {
            return 1.0;
        } else {
            return x;
        }
    }

    private static double blend(double fromValue, double toValue, double percent) {
        return fromValue + (toValue - fromValue)*percent;
    }

    // ========
    // Animates interface
    // ========

    @Override
    public boolean isAnimating() {
        return timeline != null && !timeline.isDone();
    }

    @Override
    public Color animateStop() {
        if (timeline != null && !timeline.isDone()) {
            timeline.abort();
        }
        timeline = null;
        return this;
    }

    @Override
    public Color animateStart(ReadonlyColor toColor, long durationMs) {
        if (toColor == null) {
            throw new IllegalArgumentException("toColor cannot be null");
        }
        newTimeline(toColor.getRed(), toColor.getGreen(), toColor.getBlue(), toColor.getAlpha(), durationMs);
        timeline.play();
        return this;
    }
    /**
     * Convenience method, equivalent to
     * <code>animateStart(new Color(toRed, toGreen, toBlue, toAlpha), durationMs)</code>
     */
    public Color animateStart(double toRed, double toGreen, double toBlue, double toAlpha, long durationMs) {
        newTimeline(toRed, toGreen, toBlue, toAlpha, durationMs);
        timeline.play();
        return this;
    }

    @Override
    public Color animateStartLoop(ReadonlyColor toColor, boolean reverse, long durationMs) {
        if (toColor == null) {
            throw new IllegalArgumentException("toColor cannot be null");
        }
        newTimeline(toColor.getRed(), toColor.getGreen(), toColor.getBlue(), toColor.getAlpha(), durationMs);
        timeline.playLoop(reverse);
        return this;
    }
    /**
     * Convenience method, equivalent to
     * </code>animateStartLoop(new Color(toRed, toGreen, toBlue, toAlpha), reverse, durationMs)</code>
     */
    public Color animateStartLoop(double toRed, double toGreen, double toBlue, double toAlpha, boolean reverse, long durationMs) {
        newTimeline(toRed, toGreen, toBlue, toAlpha, durationMs);
        timeline.playLoop(reverse);
        return this;
    }

    private void newTimeline(double toRed, double toGreen, double toBlue, double toAlpha, long durationMs) {
        animateStop();
        timeline = new Timeline(this);
        timeline.addPropertyToInterpolate("red",   red,   toRed);
        timeline.addPropertyToInterpolate("green", green, toGreen);
        timeline.addPropertyToInterpolate("blue",  blue,  toBlue);
        timeline.addPropertyToInterpolate("alpha", alpha, toAlpha);
        timeline.setDuration(durationMs);
    }

    // ========
    // Named colors and other static methods
    // ========

    // Define all extended web colors (W3C)
    /** <div style="background:#f0f8ff">#f0f8ff</div> */
    public static final ReadonlyColor ALICE_BLUE               = new Color(0xf0f8ffff);
    /** <div style="background:#faebd7">#faebd7</div> */
    public static final ReadonlyColor ANTIQUE_WHITE            = new Color(0xfaebd7ff);
    /** <div style="background:#00ffff">#00ffff</div> */
    public static final ReadonlyColor AQUA                     = new Color(0x00ffffff);
    /** <div style="background:#7fffd4">#7fffd4</div> */
    public static final ReadonlyColor AQUAMARINE               = new Color(0x7fffd4ff);
    /** <div style="background:#f0ffff">#f0ffff</div> */
    public static final ReadonlyColor AZURE                    = new Color(0xf0ffffff);
    /** <div style="background:#f5f5dc">#f5f5dc</div> */
    public static final ReadonlyColor BEIGE                    = new Color(0xf5f5dcff);
    /** <div style="background:#ffe4c4">#ffe4c4</div> */
    public static final ReadonlyColor BISQUE                   = new Color(0xffe4c4ff);
    /** <div style="background:#000000">#000000</div> */
    public static final ReadonlyColor BLACK                    = new Color(0x000000ff);
    /** <div style="background:#ffebcd">#ffebcd</div> */
    public static final ReadonlyColor BLANCHED_ALMOND          = new Color(0xffebcdff);
    /** <div style="background:#0000ff">#0000ff</div> */
    public static final ReadonlyColor BLUE                     = new Color(0x0000ffff);
    /** <div style="background:#8a2be2">#8a2be2</div> */
    public static final ReadonlyColor BLUE_VIOLET              = new Color(0x8a2be2ff);
    /** <div style="background:#a52a2a">#a52a2a</div> */
    public static final ReadonlyColor BROWN                    = new Color(0xa52a2aff);
    /** <div style="background:#deb887">#deb887</div> */
    public static final ReadonlyColor BURLY_WOOD               = new Color(0xdeb887ff);
    /** <div style="background:#5f9ea0">#5f9ea0</div> */
    public static final ReadonlyColor CADET_BLUE               = new Color(0x5f9ea0ff);
    /** <div style="background:#7fff00">#7fff00</div> */
    public static final ReadonlyColor CHARTREUSE               = new Color(0x7fff00ff);
    /** <div style="background:#d2691e">#d2691e</div> */
    public static final ReadonlyColor CHOCOLATE                = new Color(0xd2691eff);
    /** <div style="background:#ff7f50">#ff7f50</div> */
    public static final ReadonlyColor CORAL                    = new Color(0xff7f50ff);
    /** <div style="background:#6495ed">#6495ed</div> */
    public static final ReadonlyColor CORNFLOWER_BLUE          = new Color(0x6495edff);
    /** <div style="background:#fff8dc">#fff8dc</div> */
    public static final ReadonlyColor CORNSILK                 = new Color(0xfff8dcff);
    /** <div style="background:#dc143c">#dc143c</div> */
    public static final ReadonlyColor CRIMSON                  = new Color(0xdc143cff);
    public static final ReadonlyColor CYAN = AQUA;
    /** <div style="background:#00008b">#00008b</div> */
    public static final ReadonlyColor DARK_BLUE                = new Color(0x00008bff);
    /** <div style="background:#008b8b">#008b8b</div> */
    public static final ReadonlyColor DARK_CYAN                = new Color(0x008b8bff);
    /** <div style="background:#b8860b">#b8860b</div> */
    public static final ReadonlyColor DARK_GOLDENROD           = new Color(0xb8860bff);
    /** <div style="background:#a9a9a9">#a9a9a9</div> */
    public static final ReadonlyColor DARK_GRAY                = new Color(0xa9a9a9ff);
    /** <div style="background:#006400">#006400</div> */
    public static final ReadonlyColor DARK_GREEN               = new Color(0x006400ff);
    /** <div style="background:#a9a9a9">#a9a9a9</div> */
    public static final ReadonlyColor DARK_GREY = DARK_GRAY;
    /** <div style="background:#bdb76b">#bdb76b</div> */
    public static final ReadonlyColor DARK_KHAKI               = new Color(0xbdb76bff);
    /** <div style="background:#8b008b">#8b008b</div> */
    public static final ReadonlyColor DARK_MAGENTA             = new Color(0x8b008bff);
    /** <div style="background:#556b2f">#556b2f</div> */
    public static final ReadonlyColor DARK_OLIVE_GREEN         = new Color(0x556b2fff);
    /** <div style="background:#ff8c00">#ff8c00</div> */
    public static final ReadonlyColor DARK_ORANGE              = new Color(0xff8c00ff);
    /** <div style="background:#9932cc">#9932cc</div> */
    public static final ReadonlyColor DARK_ORCHID              = new Color(0x9932ccff);
    /** <div style="background:#8b0000">#8b0000</div> */
    public static final ReadonlyColor DARK_RED                 = new Color(0x8b0000ff);
    /** <div style="background:#e9967a">#e9967a</div> */
    public static final ReadonlyColor DARK_SALMON              = new Color(0xe9967aff);
    /** <div style="background:#8fbc8f">#8fbc8f</div> */
    public static final ReadonlyColor DARK_SEA_GREEN           = new Color(0x8fbc8fff);
    /** <div style="background:#483d8b">#483d8b</div> */
    public static final ReadonlyColor DARK_SLATE_BLUE          = new Color(0x483d8bff);
    /** <div style="background:#2f4f4f">#2f4f4f</div> */
    public static final ReadonlyColor DARK_SLATE_GRAY          = new Color(0x2f4f4fff);
    /** <div style="background:#2f4f4f">#2f4f4f</div> */
    public static final ReadonlyColor DARK_SLATE_GREY = DARK_SLATE_GRAY;
    /** <div style="background:#00ced1">#00ced1</div> */
    public static final ReadonlyColor DARK_TURQUOISE           = new Color(0x00ced1ff);
    /** <div style="background:#9400d3">#9400d3</div> */
    public static final ReadonlyColor DARK_VIOLET              = new Color(0x9400d3ff);
    /** <div style="background:#ff1493">#ff1493</div> */
    public static final ReadonlyColor DEEP_PINK                = new Color(0xff1493ff);
    /** <div style="background:#00bfff">#00bfff</div> */
    public static final ReadonlyColor DEEP_SKY_BLUE            = new Color(0x00bfffff);
    /** <div style="background:#696969">#696969</div> */
    public static final ReadonlyColor DIM_GRAY                 = new Color(0x696969ff);
    /** <div style="background:#696969">#696969</div> */
    public static final ReadonlyColor DIM_GREY = DIM_GRAY;
    /** <div style="background:#1e90ff">#1e90ff</div> */
    public static final ReadonlyColor DODGER_BLUE              = new Color(0x1e90ffff);
    /** <div style="background:#b22222">#b22222</div> */
    public static final ReadonlyColor FIRE_BRICK               = new Color(0xb22222ff);
    /** <div style="background:#fffaf0">#fffaf0</div> */
    public static final ReadonlyColor FLORAL_WHITE             = new Color(0xfffaf0ff);
    /** <div style="background:#228b22">#228b22</div> */
    public static final ReadonlyColor FOREST_GREEN             = new Color(0x228b22ff);
    /** <div style="background:#ff00ff">#ff00ff</div> */
    public static final ReadonlyColor FUCHSIA                  = new Color(0xff00ffff);
    /** <div style="background:#dcdcdc">#dcdcdc</div> */
    public static final ReadonlyColor GAINSBORO                = new Color(0xdcdcdcff);
    /** <div style="background:#f8f8ff">#f8f8ff</div> */
    public static final ReadonlyColor GHOST_WHITE              = new Color(0xf8f8ffff);
    /** <div style="background:#ffd700">#ffd700</div> */
    public static final ReadonlyColor GOLD                     = new Color(0xffd700ff);
    /** <div style="background:#daa520">#daa520</div> */
    public static final ReadonlyColor GOLDENROD                = new Color(0xdaa520ff);
    /** <div style="background:#808080">#808080</div> */
    public static final ReadonlyColor GRAY                     = new Color(0x808080ff);
    /** <div style="background:#008000">#008000</div> */
    public static final ReadonlyColor GREEN                    = new Color(0x008000ff);
    /** <div style="background:#adff2f">#adff2f</div> */
    public static final ReadonlyColor GREEN_YELLOW             = new Color(0xadff2fff);
    /** <div style="background:#808080">#808080</div> */
    public static final ReadonlyColor GREY = GRAY;
    /** <div style="background:#f0fff0">#f0fff0</div> */
    public static final ReadonlyColor HONEYDEW                 = new Color(0xf0fff0ff);
    /** <div style="background:#ff69b4">#ff69b4</div> */
    public static final ReadonlyColor HOT_PINK                 = new Color(0xff69b4ff);
    /** <div style="background:#cd5c5c">#cd5c5c</div> */
    public static final ReadonlyColor INDIAN_RED               = new Color(0xcd5c5cff);
    /** <div style="background:#4b0082">#4b0082</div> */
    public static final ReadonlyColor INDIGO                   = new Color(0x4b0082ff);
    /** <div style="background:#fffff0">#fffff0</div> */
    public static final ReadonlyColor IVORY                    = new Color(0xfffff0ff);
    /** <div style="background:#f0e68c">#f0e68c</div> */
    public static final ReadonlyColor KHAKI                    = new Color(0xf0e68cff);
    /** <div style="background:#e6e6fa">#e6e6fa</div> */
    public static final ReadonlyColor LAVENDER                 = new Color(0xe6e6faff);
    /** <div style="background:#fff0f5">#fff0f5</div> */
    public static final ReadonlyColor LAVENDER_BLUSH           = new Color(0xfff0f5ff);
    /** <div style="background:#7cfc00">#7cfc00</div> */
    public static final ReadonlyColor LAWN_GREEN               = new Color(0x7cfc00ff);
    /** <div style="background:#fffacd">#fffacd</div> */
    public static final ReadonlyColor LEMON_CHIFFON            = new Color(0xfffacdff);
    /** <div style="background:#add8e6">#add8e6</div> */
    public static final ReadonlyColor LIGHT_BLUE               = new Color(0xadd8e6ff);
    /** <div style="background:#f08080">#f08080</div> */
    public static final ReadonlyColor LIGHT_CORAL              = new Color(0xf08080ff);
    /** <div style="background:#e0ffff">#e0ffff</div> */
    public static final ReadonlyColor LIGHT_CYAN               = new Color(0xe0ffffff);
    /** <div style="background:#fafad2">#fafad2</div> */
    public static final ReadonlyColor LIGHT_GOLDENROD_YELLOW   = new Color(0xfafad2ff);
    /** <div style="background:#d3d3d3">#d3d3d3</div> */
    public static final ReadonlyColor LIGHT_GRAY               = new Color(0xd3d3d3ff);
    /** <div style="background:#90ee90">#90ee90</div> */
    public static final ReadonlyColor LIGHT_GREEN              = new Color(0x90ee90ff);
    /** <div style="background:#d3d3d3">#d3d3d3</div> */
    public static final ReadonlyColor LIGHT_GREY = LIGHT_GRAY;
    /** <div style="background:#ffb6c1">#ffb6c1</div> */
    public static final ReadonlyColor LIGHT_PINK               = new Color(0xffb6c1ff);
    /** <div style="background:#ffa07a">#ffa07a</div> */
    public static final ReadonlyColor LIGHT_SALMON             = new Color(0xffa07aff);
    /** <div style="background:#20b2aa">#20b2aa</div> */
    public static final ReadonlyColor LIGHT_SEA_GREEN          = new Color(0x20b2aaff);
    /** <div style="background:#87cefa">#87cefa</div> */
    public static final ReadonlyColor LIGHT_SKY_BLUE           = new Color(0x87cefaff);
    /** <div style="background:#778899">#778899</div> */
    public static final ReadonlyColor LIGHT_SLATE_GRAY         = new Color(0x778899ff);
    /** <div style="background:#778899">#778899</div> */
    public static final ReadonlyColor LIGHT_SLATE_GREY = LIGHT_SLATE_GRAY;
    /** <div style="background:#b0c4de">#b0c4de</div> */
    public static final ReadonlyColor LIGHT_STEEL_BLUE         = new Color(0xb0c4deff);
    /** <div style="background:#ffffe0">#ffffe0</div> */
    public static final ReadonlyColor LIGHT_YELLOW             = new Color(0xffffe0ff);
    /** <div style="background:#00ff00">#00ff00</div> */
    public static final ReadonlyColor LIME                     = new Color(0x00ff00ff);
    /** <div style="background:#32cd32">#32cd32</div> */
    public static final ReadonlyColor LIME_GREEN               = new Color(0x32cd32ff);
    /** <div style="background:#faf0e6">#faf0e6</div> */
    public static final ReadonlyColor LINEN                    = new Color(0xfaf0e6ff);
    /** <div style="background:#ff00ff">#ff00ff</div> */
    public static final ReadonlyColor MAGENTA = FUCHSIA;
    /** <div style="background:#800000">#800000</div> */
    public static final ReadonlyColor MAROON                   = new Color(0x800000ff);
    /** <div style="background:#66cdaa">#66cdaa</div> */
    public static final ReadonlyColor MEDIUM_AQUAMARINE        = new Color(0x66cdaaff);
    /** <div style="background:#0000cd">#0000cd</div> */
    public static final ReadonlyColor MEDIUM_BLUE              = new Color(0x0000cdff);
    /** <div style="background:#ba55d3">#ba55d3</div> */
    public static final ReadonlyColor MEDIUM_ORCHID            = new Color(0xba55d3ff);
    /** <div style="background:#9370db">#9370db</div> */
    public static final ReadonlyColor MEDIUM_PURPLE            = new Color(0x9370dbff);
    /** <div style="background:#3cb371">#3cb371</div> */
    public static final ReadonlyColor MEDIUM_SEA_GREEN         = new Color(0x3cb371ff);
    /** <div style="background:#7b68ee">#7b68ee</div> */
    public static final ReadonlyColor MEDIUM_SLATE_BLUE        = new Color(0x7b68eeff);
    /** <div style="background:#00fa9a">#00fa9a</div> */
    public static final ReadonlyColor MEDIUM_SPRING_GREEN      = new Color(0x00fa9aff);
    /** <div style="background:#48d1cc">#48d1cc</div> */
    public static final ReadonlyColor MEDIUM_TURQUOISE         = new Color(0x48d1ccff);
    /** <div style="background:#c71585">#c71585</div> */
    public static final ReadonlyColor MEDIUM_VIOLET_RED        = new Color(0xc71585ff);
    /** <div style="background:#191970">#191970</div> */
    public static final ReadonlyColor MIDNIGHT_BLUE            = new Color(0x191970ff);
    /** <div style="background:#f5fffa">#f5fffa</div> */
    public static final ReadonlyColor MINT_CREAM               = new Color(0xf5fffaff);
    /** <div style="background:#ffe4e1">#ffe4e1</div> */
    public static final ReadonlyColor MISTY_ROSE               = new Color(0xffe4e1ff);
    /** <div style="background:#ffe4b5">#ffe4b5</div> */
    public static final ReadonlyColor MOCCASIN                 = new Color(0xffe4b5ff);
    /** <div style="background:#ffdead">#ffdead</div> */
    public static final ReadonlyColor NAVAJO_WHITE             = new Color(0xffdeadff);
    /** <div style="background:#000080">#000080</div> */
    public static final ReadonlyColor NAVY                     = new Color(0x000080ff);
    /** <div style="background:#fdf5e6">#fdf5e6</div> */
    public static final ReadonlyColor OLD_LACE                 = new Color(0xfdf5e6ff);
    /** <div style="background:#808000">#808000</div> */
    public static final ReadonlyColor OLIVE                    = new Color(0x808000ff);
    /** <div style="background:#6b8e23">#6b8e23</div> */
    public static final ReadonlyColor OLIVE_DRAB               = new Color(0x6b8e23ff);
    /** <div style="background:#ffa500">#ffa500</div> */
    public static final ReadonlyColor ORANGE                   = new Color(0xffa500ff);
    /** <div style="background:#ff4500">#ff4500</div> */
    public static final ReadonlyColor ORANGE_RED               = new Color(0xff4500ff);
    /** <div style="background:#da70d6">#da70d6</div> */
    public static final ReadonlyColor ORCHID                   = new Color(0xda70d6ff);
    /** <div style="background:#eee8aa">#eee8aa</div> */
    public static final ReadonlyColor PALE_GOLDENROD           = new Color(0xeee8aaff);
    /** <div style="background:#98fb98">#98fb98</div> */
    public static final ReadonlyColor PALE_GREEN               = new Color(0x98fb98ff);
    /** <div style="background:#afeeee">#afeeee</div> */
    public static final ReadonlyColor PALE_TURQUOISE           = new Color(0xafeeeeff);
    /** <div style="background:#db7093">#db7093</div> */
    public static final ReadonlyColor PALE_VIOLET_RED          = new Color(0xdb7093ff);
    /** <div style="background:#ffefd5">#ffefd5</div> */
    public static final ReadonlyColor PAPAYA_WHIP              = new Color(0xffefd5ff);
    /** <div style="background:#ffdab9">#ffdab9</div> */
    public static final ReadonlyColor PEACH_PUFF               = new Color(0xffdab9ff);
    /** <div style="background:#cd853f">#cd853f</div> */
    public static final ReadonlyColor PERU                     = new Color(0xcd853fff);
    /** <div style="background:#ffc0cb">#ffc0cb</div> */
    public static final ReadonlyColor PINK                     = new Color(0xffc0cbff);
    /** <div style="background:#dda0dd">#dda0dd</div> */
    public static final ReadonlyColor PLUM                     = new Color(0xdda0ddff);
    /** <div style="background:#b0e0e6">#b0e0e6</div> */
    public static final ReadonlyColor POWDER_BLUE              = new Color(0xb0e0e6ff);
    /** <div style="background:#800080">#800080</div> */
    public static final ReadonlyColor PURPLE                   = new Color(0x800080ff);
    /** <div style="background:#ff0000">#ff0000</div> */
    public static final ReadonlyColor RED                      = new Color(0xff0000ff);
    /** <div style="background:#bc8f8f">#bc8f8f</div> */
    public static final ReadonlyColor ROSY_BROWN               = new Color(0xbc8f8fff);
    /** <div style="background:#4169e1">#4169e1</div> */
    public static final ReadonlyColor ROYAL_BLUE               = new Color(0x4169e1ff);
    /** <div style="background:#8b4513">#8b4513</div> */
    public static final ReadonlyColor SADDLE_BROWN             = new Color(0x8b4513ff);
    /** <div style="background:#fa8072">#fa8072</div> */
    public static final ReadonlyColor SALMON                   = new Color(0xfa8072ff);
    /** <div style="background:#f4a460">#f4a460</div> */
    public static final ReadonlyColor SANDY_BROWN              = new Color(0xf4a460ff);
    /** <div style="background:#2e8b57">#2e8b57</div> */
    public static final ReadonlyColor SEA_GREEN                = new Color(0x2e8b57ff);
    /** <div style="background:#fff5ee">#fff5ee</div> */
    public static final ReadonlyColor SEASHELL                 = new Color(0xfff5eeff);
    /** <div style="background:#a0522d">#a0522d</div> */
    public static final ReadonlyColor SIENNA                   = new Color(0xa0522dff);
    /** <div style="background:#c0c0c0">#c0c0c0</div> */
    public static final ReadonlyColor SILVER                   = new Color(0xc0c0c0ff);
    /** <div style="background:#87ceeb">#87ceeb</div> */
    public static final ReadonlyColor SKY_BLUE                 = new Color(0x87ceebff);
    /** <div style="background:#6a5acd">#6a5acd</div> */
    public static final ReadonlyColor SLATE_BLUE               = new Color(0x6a5acdff);
    /** <div style="background:#708090">#708090</div> */
    public static final ReadonlyColor SLATE_GRAY               = new Color(0x708090ff);
    /** <div style="background:#708090">#708090</div> */
    public static final ReadonlyColor SLATE_GREY = SLATE_GRAY;
    /** <div style="background:#fffafa">#fffafa</div> */
    public static final ReadonlyColor SNOW                     = new Color(0xfffafaff);
    /** <div style="background:#00ff7f">#00ff7f</div> */
    public static final ReadonlyColor SPRING_GREEN             = new Color(0x00ff7fff);
    /** <div style="background:#4682b4">#4682b4</div> */
    public static final ReadonlyColor STEEL_BLUE               = new Color(0x4682b4ff);
    /** <div style="background:#d2b48c">#d2b48c</div> */
    public static final ReadonlyColor TAN                      = new Color(0xd2b48cff);
    /** <div style="background:#008080">#008080</div> */
    public static final ReadonlyColor TEAL                     = new Color(0x008080ff);
    /** <div style="background:#d8bfd8">#d8bfd8</div> */
    public static final ReadonlyColor THISTLE                  = new Color(0xd8bfd8ff);
    /** <div style="background:#ff6347">#ff6347</div> */
    public static final ReadonlyColor TOMATO                   = new Color(0xff6347ff);
    /** <div style="background:#40e0d0">#40e0d0</div> */
    public static final ReadonlyColor TURQUOISE                = new Color(0x40e0d0ff);
    /** <div style="background:#ee82ee">#ee82ee</div> */
    public static final ReadonlyColor VIOLET                   = new Color(0xee82eeff);
    /** <div style="background:#f5deb3">#f5deb3</div> */
    public static final ReadonlyColor WHEAT                    = new Color(0xf5deb3ff);
    /** <div style="background:#ffffff">#ffffff</div> */
    public static final ReadonlyColor WHITE                    = new Color(0xffffffff);
    /** <div style="background:#f5f5f5">#f5f5f5</div> */
    public static final ReadonlyColor WHITE_SMOKE              = new Color(0xf5f5f5ff);
    /** <div style="background:#ffff00">#ffff00</div> */
    public static final ReadonlyColor YELLOW                   = new Color(0xffff00ff);
    /** <div style="background:#9acd32">#9acd32</div> */
    public static final ReadonlyColor YELLOW_GREEN             = new Color(0x9acd32ff);
    // Bob Ross approves

    // Other useful named colors
    public static final ReadonlyColor TRANSPARENT_BLACK = new Color(0x00000000);
    public static final ReadonlyColor TRANSPARENT_WHITE = new Color(0xffffff00);

    private static HashMap<String, ReadonlyColor> namedColors;
    /**
     * @return the ReadonlyColor matching the name parameter, or null if there
     *         is no such named color. Insensitive to case, whitespace, and
     *         underscores.
     *         <p>
     *         All <a href="http://en.wikipedia.org/wiki/Web_colors">W3C/X11
     *         named colors</a> are included.
     */
    public static ReadonlyColor getNamedColor(String name) {
        if (name == null) {
            return null;
        }
        if (namedColors == null) {
            // lazily instantiate
            namedColors = new HashMap<String, ReadonlyColor>();
            try {
                for (Field field : Color.class.getDeclaredFields()) {
                    if (field.getType() == ReadonlyColor.class) {
                        namedColors.put(field.getName().replaceAll("_", ""), (ReadonlyColor) field.get(null));
                    }
                }
            } catch (Exception e) {
                throw new LSDInternalReflectionException("unable to reflect named colors", e);
            }
        }
        return namedColors.get(name.toUpperCase().replaceAll("[_\\s]", ""));
    }

    /**
     * Change the byte order of a 32-bit integer packed with color components.
     */
    public static int convertARGBtoRGBA(int argb) {
        return (argb << 8) | ((argb & 0xff000000) >>> 24);
    }

    /**
     * Change the byte order of a 32-bit integer packed with color components.
     */
    public static int convertRGBAtoARGB(int rgba) {
        return (rgba >>> 8) | ((rgba & 0x000000ff) << 24);
    }
}
