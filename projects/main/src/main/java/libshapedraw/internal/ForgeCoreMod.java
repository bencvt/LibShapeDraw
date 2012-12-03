package libshapedraw.internal;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

/**
 * To provide compatibility with different install scenarios, this internal
 * class (coupled with an entry in the manifest; see pom.xml) marks
 * LibShapeDraw as an FML coremod.
 * <p>
 * FML coremods can do a number of things that regular FML mods cannot, such as
 * manipulating bytecode. We don't have any use for that feature, so this class
 * is just a dummy implementation.
 * <p>
 * The coremod feature we *do* need is the ability to be visible to other mods.
 * Any jars/zips (a.k.a. mod containers) that FML finds in the "mods" directory
 * are loaded, but the mods inside are isolated from mods that live in other
 * containers.
 * <p>
 * Obviously, as an API we need to *not* be isolated.
 * <p>
 * To break the isolation, the LibShapeDraw jar needs to live in FML's special
 * "coremods" directory. We also need to implement IFMLLoadingPlugin and add a
 * FMLCorePlugin entry to the jar's manifest. We jump through those hoops to
 * support users who prefer to install their mods this way.
 * <p>
 * Note that this class will never be loaded for non-FML (i.e., good
 * old-fashioned ModLoader) installs. ModLoader does not differentiate between
 * mods and coremods, and does not enforce classpath isolation. Placing the jar
 * in the "mods" directory works fine for ModLoader installation.
 */
public class ForgeCoreMod implements IFMLLoadingPlugin {
    @Override
    public String[] getLibraryRequestClass() {
        return null;
    }

    @Override
    public String[] getASMTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        // do nothing
    }
}
