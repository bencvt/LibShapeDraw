package libshapedraw.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Internal singleton reference class listing all settings affecting the
 * controller itself (and thus all instances of the API).
 * <p>
 * The settings are contained in a properties file that lives in the jar. The
 * end user can override these settings by placing an appropriately-named
 * properties file in the mod's directory.
 */
public class LSDGlobalSettings {
    public static boolean isLoggingEnabled() {
        return getInstance().loggingEnabled;
    }
    public static boolean isLoggingAppend() {
        return getInstance().loggingAppend;
    }
    public static int getLoggingDebugDumpInterval() {
        return getInstance().loggingDebugDumpInterval;
    }
    public static boolean isUpdateCheckEnabled() {
        return getInstance().updateCheckEnabled;
    }

    private final boolean loggingEnabled;
    private final boolean loggingAppend;
    private final int loggingDebugDumpInterval;
    private final boolean updateCheckEnabled;

    private static LSDGlobalSettings instance;

    private LSDGlobalSettings() {
        try {
            Properties props = new Properties();
            try {
                InputStream in;
                File file = new File(LSDModDirectory.DIRECTORY, "settings.properties");
                if (file.exists()) {
                    in = new FileInputStream(file);
                } else {
                    in = getClass().getResourceAsStream("default-settings.properties");
                }
                props.load(in);
                in.close();
            } catch (IOException e) {
                throw new LSDInternalException("unable to load resource", e);
            }
            loggingEnabled = LSDUtil.parseBooleanStrict(props.getProperty("logging-enabled"));
            loggingAppend = LSDUtil.parseBooleanStrict(props.getProperty("logging-append"));
            loggingDebugDumpInterval = Integer.parseInt(props.getProperty("logging-debug-dump-interval"));
            updateCheckEnabled = LSDUtil.parseBooleanStrict(props.getProperty("update-check-enabled"));
        } catch (Exception e) {
            e.printStackTrace();
            throw new LSDInternalException("unable to load global settings", e);
        }
    }

    private static LSDGlobalSettings getInstance() {
        if (instance == null) {
            instance = new LSDGlobalSettings();
        }
        return instance;
    }
}
