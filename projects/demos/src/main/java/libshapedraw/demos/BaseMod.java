package libshapedraw.demos;

/**
 * Fake ModLoader BaseMod, here in the same package as the demo "mods".
 */
public abstract class BaseMod {
    public abstract String getVersion();
    public abstract void load();
}
