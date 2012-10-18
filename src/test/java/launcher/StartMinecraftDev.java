package launcher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import libshapedraw.internal.LSDInternalException;

import net.minecraft.client.Minecraft;

/**
 * Utility class to start Minecraft in a development environment.
 * <p>
 * You'll need to run this once to setup ModLoader before it can load any mods
 * (including LibDrawShapes).
 * <p>
 * Suggested VM arguments:
 * -Djava.library.path=lib/natives -Xmx1G -Xms1G
 */
public class StartMinecraftDev {
    public static void main(String[] args) {
        try {
            File mcdev = new File("mcdev");

            // Change the minecraft data folder to a portable location.
            Field dataFolder = Minecraft.class.getDeclaredField("am");
            if (dataFolder.getType() != File.class) {
                throw new LSDInternalException("expected File field not found on Minecraft; obfuscation may be out of date");
            }
            dataFolder.setAccessible(true);
            dataFolder.set(null, mcdev);

            if (mcdev.exists()) {
                // Force ModLoader to recognize mods outside of minecraft.jar.
                // Make sure you've built the LibShapeDraw jar with Maven already.
                Method readFromClassPath = Class.forName("ModLoader").getDeclaredMethod("readFromClassPath", File.class);
                readFromClassPath.setAccessible(true);
                for (File jar : new File("target").listFiles()) {
                    if (jar.isFile() && jar.getName().endsWith(".jar")) {
                        readFromClassPath.invoke(null, jar);
                    }
                }
            }
            // Else ModLoader will crash and burn. Not worth the time digging
            // into its internals to figure out why -- the first time you run
            // this launcher, just start and stop it once to get everything
            // initialized, and then it's fine.

            Minecraft.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
