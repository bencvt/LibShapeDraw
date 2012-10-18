package libshapedraw;

import java.io.File;
import java.lang.reflect.Field;

import libshapedraw.internal.LSDController;
import libshapedraw.internal.LSDInternalException;
import libshapedraw.internal.LSDInternalReflectionException;
import libshapedraw.internal.LSDModDirectory;
import libshapedraw.internal.LSDUtil;

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
    private static final String MODDIRECTORY_CLASS_NAME = "libshapedraw.internal.LSDModDirectory";
    private static final String MODDIRECTORY_FIELD_NAME = "DIRECTORY";

    /**
     * If you're running one of these test case classes individually in Eclipse or
     * another IDE, you'll probably need to add "-Djava.library.path=lib/natives" to
     * the VM arguments.
     */
    public static class TestCase {
        protected static final MockMinecraftAccess mockMinecraftAccess = new MockMinecraftAccess();

        @BeforeClass
        public static void setupTestEnvironment() throws LWJGLException {
            if (setup()) {
                LSDController.getInstance().initialize(mockMinecraftAccess);
                Display.setDisplayMode(new DisplayMode(0, 0));
                Display.create();
            }
        }
    }

    private static boolean setup() {
        println("setup test environment");
        if (testMinecraftDirectory == null) {
            testMinecraftDirectory = LSDUtil.createTempDirectory("LibShapeDrawJUnitTemp");
            monkeyPatch();
            return true;
        }
        // else we've already monkey patched, as we're running an entire test suite
        return false;
    }

    private static void monkeyPatch() {
        if (LSDUtil.isClassLoaded(MODDIRECTORY_CLASS_NAME)) {
            throw new LSDInternalException("internal error, " + MODDIRECTORY_CLASS_NAME + " already loaded");
        }

        // Force ModDirectory class load and monkey patch ModDirectory.DIRECTORY.
        File origDir = LSDModDirectory.DIRECTORY;
        Field field;
        try {
            field = LSDModDirectory.class.getDeclaredField(MODDIRECTORY_FIELD_NAME);
        } catch (Exception e) {
            throw new LSDInternalReflectionException("unable to get field named " + MODDIRECTORY_FIELD_NAME, e);
        }
        LSDUtil.setFinalField(field, null, testMinecraftDirectory);

        println("monkey patched directory field from:\n  " + origDir + "\nto:\n  " + testMinecraftDirectory);

        if (!LSDModDirectory.class.getName().equals(MODDIRECTORY_CLASS_NAME)
                || !LSDUtil.isClassLoaded(MODDIRECTORY_CLASS_NAME)
                || !LSDModDirectory.DIRECTORY.equals(testMinecraftDirectory)) {
            throw new LSDInternalException("internal error, sanity check failed");
        }
    }

    private static void println(String msg) {
        System.out.println(msg);
    }
}
