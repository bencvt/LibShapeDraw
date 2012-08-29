package libshapedraw.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Internal reference class listing all settings affecting the controller itself
 * (and thus all instances of the API).
 * <p>
 * The settings are contained in a properties file that lives in the jar. The end user
 * can override these settings by placing an appropriately-named properties file in
 * the mod's directory.
 */
public class GlobalSettings {
    public static boolean isLoggingEnabled() {
        return getInstance().loggingEnabled;
    }
    public static boolean isAppendLog() {
        return getInstance().appendLog;
    }
    public static int getGhostEntityUpdateTicks() {
        return getInstance().ghostEntityUpdateTicks;
    }
    public static int getDumpInterval() {
        return getInstance().dumpInterval;
    }

    private final boolean loggingEnabled;
    private final boolean appendLog;
    private final int ghostEntityUpdateTicks;
    private final int dumpInterval;

    private static GlobalSettings instance;

    private GlobalSettings() {
        try {
            Properties props = new Properties();
            try {
                InputStream in;
                File file = new File(ModDirectory.DIRECTORY, "settings.properties");
                if (file.exists()) {
                    in = new FileInputStream(file);
                } else {
                    in = getClass().getResourceAsStream("default-settings.properties");
                }
                props.load(in);
                in.close();
            } catch (IOException e) {
                throw new RuntimeException("unable to load resource", e);
            }
            loggingEnabled = Util.parseBooleanStrict(props.getProperty("logging-enabled"));
            appendLog = Util.parseBooleanStrict(props.getProperty("logging-append"));
            ghostEntityUpdateTicks = Integer.parseInt(props.getProperty("ghost-entity-update-ticks"));
            dumpInterval = Integer.parseInt(props.getProperty("debug-dump-interval"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("unable to load global settings", e);
        }
    }

    private static GlobalSettings getInstance() {
        if (instance == null) {
            instance = new GlobalSettings();
        }
        return instance;
    }
}
