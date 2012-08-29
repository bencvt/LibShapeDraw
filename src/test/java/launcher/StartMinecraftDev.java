package launcher;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;

/**
 * Utility class to start Minecraft in a development environment.
 * Suggested VM arguments:
 * -Djava.library.path=lib/natives -Xmx1G -Xms1G
 */
public class StartMinecraftDev {
    public static void main(String[] args) {
        try {
            // Change the minecraft data folder to a portable location.
            Field dataFolder = Minecraft.class.getDeclaredField("am");
            dataFolder.setAccessible(true);
            dataFolder.set(null, new File("mcdev"));

            // Force ModLoader to recognize mods outside of minecraft.jar.
            // Make sure you've built the LibShapeDraw jar with Maven already.
            Method readFromClassPath = Class.forName("ModLoader").getDeclaredMethod("readFromClassPath", File.class);
            readFromClassPath.setAccessible(true);
            for (File jar : new File("target").listFiles()) {
                if (jar.isFile() && jar.getName().endsWith(".jar")) {
                    readFromClassPath.invoke(null, jar);
                }
            }

            Minecraft.main(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
