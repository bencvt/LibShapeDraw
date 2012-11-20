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
    private double red;
    private double green;
    private double blue;
    private double alpha;
    /** @see Vector3#timeline */
    private Timeline timeline;

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
    public static final ReadonlyColor ALICE_BLUE               = new Color(0xf0f8ffff);
    public static final ReadonlyColor ANTIQUE_WHITE            = new Color(0xfaebd7ff);
    public static final ReadonlyColor AQUA                     = new Color(0x00ffffff);
    public static final ReadonlyColor AQUAMARINE               = new Color(0x7fffd4ff);
    public static final ReadonlyColor AZURE                    = new Color(0xf0ffffff);
    public static final ReadonlyColor BEIGE                    = new Color(0xf5f5dcff);
    public static final ReadonlyColor BISQUE                   = new Color(0xffe4c4ff);
    public static final ReadonlyColor BLACK                    = new Color(0x000000ff);
    public static final ReadonlyColor BLANCHED_ALMOND          = new Color(0xffebcdff);
    public static final ReadonlyColor BLUE                     = new Color(0x0000ffff);
    public static final ReadonlyColor BLUE_VIOLET              = new Color(0x8a2be2ff);
    public static final ReadonlyColor BROWN                    = new Color(0xa52a2aff);
    public static final ReadonlyColor BURLY_WOOD               = new Color(0xdeb887ff);
    public static final ReadonlyColor CADET_BLUE               = new Color(0x5f9ea0ff);
    public static final ReadonlyColor CHARTREUSE               = new Color(0x7fff00ff);
    public static final ReadonlyColor CHOCOLATE                = new Color(0xd2691eff);
    public static final ReadonlyColor CORAL                    = new Color(0xff7f50ff);
    public static final ReadonlyColor CORNFLOWER_BLUE          = new Color(0x6495edff);
    public static final ReadonlyColor CORNSILK                 = new Color(0xfff8dcff);
    public static final ReadonlyColor CRIMSON                  = new Color(0xdc143cff);
    public static final ReadonlyColor CYAN = AQUA;
    public static final ReadonlyColor DARK_BLUE                = new Color(0x00008bff);
    public static final ReadonlyColor DARK_CYAN                = new Color(0x008b8bff);
    public static final ReadonlyColor DARK_GOLDENROD           = new Color(0xb8860bff);
    public static final ReadonlyColor DARK_GRAY                = new Color(0xa9a9a9ff);
    public static final ReadonlyColor DARK_GREEN               = new Color(0x006400ff);
    public static final ReadonlyColor DARK_GREY = DARK_GRAY;
    public static final ReadonlyColor DARK_KHAKI               = new Color(0xbdb76bff);
    public static final ReadonlyColor DARK_MAGENTA             = new Color(0x8b008bff);
    public static final ReadonlyColor DARK_OLIVE_GREEN         = new Color(0x556b2fff);
    public static final ReadonlyColor DARK_ORANGE              = new Color(0xff8c00ff);
    public static final ReadonlyColor DARK_ORCHID              = new Color(0x9932ccff);
    public static final ReadonlyColor DARK_RED                 = new Color(0x8b0000ff);
    public static final ReadonlyColor DARK_SALMON              = new Color(0xe9967aff);
    public static final ReadonlyColor DARK_SEA_GREEN           = new Color(0x8fbc8fff);
    public static final ReadonlyColor DARK_SLATE_BLUE          = new Color(0x483d8bff);
    public static final ReadonlyColor DARK_SLATE_GRAY          = new Color(0x2f4f4fff);
    public static final ReadonlyColor DARK_SLATE_GREY = DARK_SLATE_GRAY;
    public static final ReadonlyColor DARK_TURQUOISE           = new Color(0x00ced1ff);
    public static final ReadonlyColor DARK_VIOLET              = new Color(0x9400d3ff);
    public static final ReadonlyColor DEEP_PINK                = new Color(0xff1493ff);
    public static final ReadonlyColor DEEP_SKY_BLUE            = new Color(0x00bfffff);
    public static final ReadonlyColor DIM_GRAY                 = new Color(0x696969ff);
    public static final ReadonlyColor DIM_GREY = DIM_GRAY;
    public static final ReadonlyColor DODGER_BLUE              = new Color(0x1e90ffff);
    public static final ReadonlyColor FIRE_BRICK               = new Color(0xb22222ff);
    public static final ReadonlyColor FLORAL_WHITE             = new Color(0xfffaf0ff);
    public static final ReadonlyColor FOREST_GREEN             = new Color(0x228b22ff);
    public static final ReadonlyColor FUCHSIA                  = new Color(0xff00ffff);
    public static final ReadonlyColor GAINSBORO                = new Color(0xdcdcdcff);
    public static final ReadonlyColor GHOST_WHITE              = new Color(0xf8f8ffff);
    public static final ReadonlyColor GOLD                     = new Color(0xffd700ff);
    public static final ReadonlyColor GOLDENROD                = new Color(0xdaa520ff);
    public static final ReadonlyColor GRAY                     = new Color(0x808080ff);
    public static final ReadonlyColor GREEN                    = new Color(0x008000ff);
    public static final ReadonlyColor GREEN_YELLOW             = new Color(0xadff2fff);
    public static final ReadonlyColor GREY = GRAY;
    public static final ReadonlyColor HONEYDEW                 = new Color(0xf0fff0ff);
    public static final ReadonlyColor HOT_PINK                 = new Color(0xff69b4ff);
    public static final ReadonlyColor INDIAN_RED               = new Color(0xcd5c5cff);
    public static final ReadonlyColor INDIGO                   = new Color(0x4b0082ff);
    public static final ReadonlyColor IVORY                    = new Color(0xfffff0ff);
    public static final ReadonlyColor KHAKI                    = new Color(0xf0e68cff);
    public static final ReadonlyColor LAVENDER                 = new Color(0xe6e6faff);
    public static final ReadonlyColor LAVENDER_BLUSH           = new Color(0xfff0f5ff);
    public static final ReadonlyColor LAWN_GREEN               = new Color(0x7cfc00ff);
    public static final ReadonlyColor LEMON_CHIFFON            = new Color(0xfffacdff);
    public static final ReadonlyColor LIGHT_BLUE               = new Color(0xadd8e6ff);
    public static final ReadonlyColor LIGHT_CORAL              = new Color(0xf08080ff);
    public static final ReadonlyColor LIGHT_CYAN               = new Color(0xe0ffffff);
    public static final ReadonlyColor LIGHT_GOLDENROD_YELLOW   = new Color(0xfafad2ff);
    public static final ReadonlyColor LIGHT_GRAY               = new Color(0xd3d3d3ff);
    public static final ReadonlyColor LIGHT_GREEN              = new Color(0x90ee90ff);
    public static final ReadonlyColor LIGHT_GREY = LIGHT_GRAY;
    public static final ReadonlyColor LIGHT_PINK               = new Color(0xffb6c1ff);
    public static final ReadonlyColor LIGHT_SALMON             = new Color(0xffa07aff);
    public static final ReadonlyColor LIGHT_SEA_GREEN          = new Color(0x20b2aaff);
    public static final ReadonlyColor LIGHT_SKY_BLUE           = new Color(0x87cefaff);
    public static final ReadonlyColor LIGHT_SLATE_GRAY         = new Color(0x778899ff);
    public static final ReadonlyColor LIGHT_SLATE_GREY = LIGHT_SLATE_GRAY;
    public static final ReadonlyColor LIGHT_STEEL_BLUE         = new Color(0xb0c4deff);
    public static final ReadonlyColor LIGHT_YELLOW             = new Color(0xffffe0ff);
    public static final ReadonlyColor LIME                     = new Color(0x00ff00ff);
    public static final ReadonlyColor LIME_GREEN               = new Color(0x32cd32ff);
    public static final ReadonlyColor LINEN                    = new Color(0xfaf0e6ff);
    public static final ReadonlyColor MAGENTA = FUCHSIA;
    public static final ReadonlyColor MAROON                   = new Color(0x800000ff);
    public static final ReadonlyColor MEDIUM_AQUAMARINE        = new Color(0x66cdaaff);
    public static final ReadonlyColor MEDIUM_BLUE              = new Color(0x0000cdff);
    public static final ReadonlyColor MEDIUM_ORCHID            = new Color(0xba55d3ff);
    public static final ReadonlyColor MEDIUM_PURPLE            = new Color(0x9370dbff);
    public static final ReadonlyColor MEDIUM_SEA_GREEN         = new Color(0x3cb371ff);
    public static final ReadonlyColor MEDIUM_SLATE_BLUE        = new Color(0x7b68eeff);
    public static final ReadonlyColor MEDIUM_SPRING_GREEN      = new Color(0x00fa9aff);
    public static final ReadonlyColor MEDIUM_TURQUOISE         = new Color(0x48d1ccff);
    public static final ReadonlyColor MEDIUM_VIOLET_RED        = new Color(0xc71585ff);
    public static final ReadonlyColor MIDNIGHT_BLUE            = new Color(0x191970ff);
    public static final ReadonlyColor MINT_CREAM               = new Color(0xf5fffaff);
    public static final ReadonlyColor MISTY_ROSE               = new Color(0xffe4e1ff);
    public static final ReadonlyColor MOCCASIN                 = new Color(0xffe4b5ff);
    public static final ReadonlyColor NAVAJO_WHITE             = new Color(0xffdeadff);
    public static final ReadonlyColor NAVY                     = new Color(0x000080ff);
    public static final ReadonlyColor OLD_LACE                 = new Color(0xfdf5e6ff);
    public static final ReadonlyColor OLIVE                    = new Color(0x808000ff);
    public static final ReadonlyColor OLIVE_DRAB               = new Color(0x6b8e23ff);
    public static final ReadonlyColor ORANGE                   = new Color(0xffa500ff);
    public static final ReadonlyColor ORANGE_RED               = new Color(0xff4500ff);
    public static final ReadonlyColor ORCHID                   = new Color(0xda70d6ff);
    public static final ReadonlyColor PALE_GOLDENROD           = new Color(0xeee8aaff);
    public static final ReadonlyColor PALE_GREEN               = new Color(0x98fb98ff);
    public static final ReadonlyColor PALE_TURQUOISE           = new Color(0xafeeeeff);
    public static final ReadonlyColor PALE_VIOLET_RED          = new Color(0xdb7093ff);
    public static final ReadonlyColor PAPAYA_WHIP              = new Color(0xffefd5ff);
    public static final ReadonlyColor PEACH_PUFF               = new Color(0xffdab9ff);
    public static final ReadonlyColor PERU                     = new Color(0xcd853fff);
    public static final ReadonlyColor PINK                     = new Color(0xffc0cbff);
    public static final ReadonlyColor PLUM                     = new Color(0xdda0ddff);
    public static final ReadonlyColor POWDER_BLUE              = new Color(0xb0e0e6ff);
    public static final ReadonlyColor PURPLE                   = new Color(0x800080ff);
    public static final ReadonlyColor RED                      = new Color(0xff0000ff);
    public static final ReadonlyColor ROSY_BROWN               = new Color(0xbc8f8fff);
    public static final ReadonlyColor ROYAL_BLUE               = new Color(0x4169e1ff);
    public static final ReadonlyColor SADDLE_BROWN             = new Color(0x8b4513ff);
    public static final ReadonlyColor SALMON                   = new Color(0xfa8072ff);
    public static final ReadonlyColor SANDY_BROWN              = new Color(0xf4a460ff);
    public static final ReadonlyColor SEA_GREEN                = new Color(0x2e8b57ff);
    public static final ReadonlyColor SEASHELL                 = new Color(0xfff5eeff);
    public static final ReadonlyColor SIENNA                   = new Color(0xa0522dff);
    public static final ReadonlyColor SILVER                   = new Color(0xc0c0c0ff);
    public static final ReadonlyColor SKY_BLUE                 = new Color(0x87ceebff);
    public static final ReadonlyColor SLATE_BLUE               = new Color(0x6a5acdff);
    public static final ReadonlyColor SLATE_GRAY               = new Color(0x708090ff);
    public static final ReadonlyColor SLATE_GREY = SLATE_GRAY;
    public static final ReadonlyColor SNOW                     = new Color(0xfffafaff);
    public static final ReadonlyColor SPRING_GREEN             = new Color(0x00ff7fff);
    public static final ReadonlyColor STEEL_BLUE               = new Color(0x4682b4ff);
    public static final ReadonlyColor TAN                      = new Color(0xd2b48cff);
    public static final ReadonlyColor TEAL                     = new Color(0x008080ff);
    public static final ReadonlyColor THISTLE                  = new Color(0xd8bfd8ff);
    public static final ReadonlyColor TOMATO                   = new Color(0xff6347ff);
    public static final ReadonlyColor TURQUOISE                = new Color(0x40e0d0ff);
    public static final ReadonlyColor VIOLET                   = new Color(0xee82eeff);
    public static final ReadonlyColor WHEAT                    = new Color(0xf5deb3ff);
    public static final ReadonlyColor WHITE                    = new Color(0xffffffff);
    public static final ReadonlyColor WHITE_SMOKE              = new Color(0xf5f5f5ff);
    public static final ReadonlyColor YELLOW                   = new Color(0xffff00ff);
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
