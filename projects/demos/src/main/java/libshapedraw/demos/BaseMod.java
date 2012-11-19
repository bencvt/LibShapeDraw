package libshapedraw.demos;

/**
 * Need to define a fake ModLoader BaseMod since we're not in the root package.
 */
public abstract class BaseMod {
    public abstract String getVersion();
    public abstract void load();
}
