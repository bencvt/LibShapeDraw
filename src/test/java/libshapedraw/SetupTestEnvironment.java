package libshapedraw;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import libshapedraw.internal.Controller;
import libshapedraw.internal.ModDirectory;
import libshapedraw.internal.Util;

import org.junit.BeforeClass;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

/**
 * To ensure that JUnit testing does not touch the production Minecraft installation,
 * every test case should extend SetupTestEnvironment.TestCase.
 * <p>
 * Involves some ClassLoader and Reflection hackery.
 */
public class SetupTestEnvironment {
    private static File testMinecraftDirectory = null;
    private static final String MODDIRECTORY_CLASS_NAME = "libshapedraw.internal.ModDirectory";
    private static final String MODDIRECTORY_FIELD_NAME = "DIRECTORY";

    /**
     * If you're running one of these test case classes individually in Eclipse or
     * another IDE, you'll probably need to add "-Djava.library.path=lib/natives" to
     * the VM arguments.
     */
    public static class TestCase {
        @BeforeClass
        public static void setupTestEnvironment() throws LWJGLException {
            if (setup()) {
                Controller.getInstance().initialize(new MockMinecraftAccess());
                Display.setDisplayMode(new DisplayMode(0, 0));
                Display.create();
            }
        }
    }

    private static boolean setup() {
        println("setup test environment");
        if (testMinecraftDirectory == null) {
            testMinecraftDirectory = Util.createTempDirectory("LibShapeDrawJUnitTemp");
            monkeyPatch();
            return true;
        }
        // else we've already monkey patched, as we're running an entire test suite
        return false;
    }

    private static void monkeyPatch() {
        if (Util.isClassLoaded(MODDIRECTORY_CLASS_NAME)) {
            throw new RuntimeException("internal error, " + MODDIRECTORY_CLASS_NAME + " already loaded");
        }

        // Force ModDirectory class load and monkey patch ModDirectory.DIRECTORY.
        // I.e. use reflection to change the static field, even though it's marked as final.
        // @see http://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
        File origDir = ModDirectory.DIRECTORY;
        try {
            Field field = ModDirectory.class.getDeclaredField(MODDIRECTORY_FIELD_NAME);
            field.setAccessible(true);
            // yo dawg
            Field fieldField = Field.class.getDeclaredField("modifiers");
            fieldField.setAccessible(true);
            fieldField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(null, testMinecraftDirectory);
        } catch (Exception e) {
            throw new RuntimeException("internal error, " + MODDIRECTORY_CLASS_NAME + " reflection failed", e);
        }

        println("monkey patched directory field from:\n  " + origDir + "\nto:\n  " + testMinecraftDirectory);

        if (!ModDirectory.class.getName().equals(MODDIRECTORY_CLASS_NAME)
                || !Util.isClassLoaded(MODDIRECTORY_CLASS_NAME)
                || !ModDirectory.DIRECTORY.equals(testMinecraftDirectory)) {
            throw new RuntimeException("internal error, sanity check failed");
        }
    }

    private static void println(String msg) {
        System.out.println(msg);
    }
}
