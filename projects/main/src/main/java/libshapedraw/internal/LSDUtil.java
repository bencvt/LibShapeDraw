package libshapedraw.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Internal class. Miscellaneous general-purpose utility classes and methods.
 */
public class LSDUtil {
    public static final Random random = new Random();

    public static class FileLogger extends Logger {
        /** Formats lines like: "2012-05-21 20:57:06.540 [INFO] example message" */
        public static class LineFormatter extends Formatter {
            @Override
            public String format(LogRecord record) {
                StringWriter w = new StringWriter();
                String ts = new Timestamp((new Date()).getTime()).toString();
                w.append(ts);
                for (int pad = ts.length(); pad < 23; pad++) {
                    w.append('0');
                }
                w.append(" [").append(record.getLevel().getName());
                w.append("] ").append(formatMessage(record)).append('\n');
                if (record.getThrown() != null) {
                    record.getThrown().printStackTrace(new PrintWriter(w));
                }
                return w.toString();
            }
        }

        public FileLogger(File baseDirectory, String name, boolean append) {
            super(name, null);
            baseDirectory.mkdirs();
            String logFilePattern = baseDirectory.getPath() + File.separator + name + ".log";
            try {
                FileHandler handler = new FileHandler(logFilePattern, append);
                handler.setFormatter(new LineFormatter());
                addHandler(handler);
            } catch (IOException e) {
                throw new LSDInternalException("unable to add file handler for " + logFilePattern, e);
            }
        }
    }

    public static class NullLogger extends Logger {
        public NullLogger() {
            super("null", null);
            setLevel(Level.OFF);
        }
    }

    /**
     * Stricter version of Boolean.parseBoolean
     */
    public static boolean parseBooleanStrict(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalArgumentException("expecting true or false, got " + value);
    }

    /**
     * Check whether a class is loaded without attempting to actually load it.
     */
    public static boolean isClassLoaded(String className) {
        try {
            Method m = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
            m.setAccessible(true);
            return m.invoke(ClassLoader.getSystemClassLoader(), className) != null;
        } catch (Exception e) {
            throw new LSDInternalReflectionException("ClassLoader.findLoadedClass reflection failed", e);
        }
    }

    /**
     * Create a uniquely-named temporary directory. This does essentially the
     * same thing as java.nio.file.Files#createTempDirectory, which we can't
     * use because it's a Java 7+ feature. Many Minecraft users are still on
     * Java 6.
     */
    public static synchronized File createTempDirectory(String prefix) {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        baseDir.mkdirs();
        while (true) {
            File tempDir = new File(baseDir, prefix + Math.abs(random.nextLong()));
            if (!tempDir.exists()) {
                if (tempDir.mkdir()) {
                    return tempDir;
                }
            }
        }
    }

    /**
     * Attempt to retrieve the contents at the specified URL as a UTF8-encoded,
     * newline-normalized string. No special handling for redirects or other
     * HTTP return codes; just a quick-and-dirty GET. Return null if anything
     * breaks.
     */
    public static String getUrlContents(URL url) {
        try {
            StringBuilder result = new StringBuilder();
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF8"));
            String line;
            while ((line = in.readLine()) != null) {
                if (result.length() > 0) {
                    result.append('\n');
                }
                result.append(line);
            }
            in.close();
            return result.toString();
        } catch (IOException e) {
            LSDController.getLog().log(Level.WARNING, "unable to read " + url, e);
            return null;
        }
    }

    /**
     * For getting at those pesky private and obfuscated fields.
     * <p>
     * Caveat: this is not guaranteed to work correctly if there are more than
     * one declared fields of the same exact type on obj.
     * <p>
     * From {@link Class#getDeclaredFields}: "The elements in the array
     * returned are not sorted and are not in any particular order."
     * <p>
     * In practice things work as expected even if there are multiple fields of
     * the same type, but this is dependent on the JVM implementation.
     * 
     * @return the nth (usually 0) field of the specified type declared by
     *         obj's class.
     */
    @SuppressWarnings("rawtypes")
    public static Field getFieldByType(Class objClass, Class fieldType, int n) {
        try {
            int index = 0;
            for (Field field : objClass.getDeclaredFields()) {
                field.setAccessible(true);
                if (field.getType().equals(fieldType)) {
                    if (index == n) {
                        return field;
                    }
                    index++;
                }
            }
            throw new LSDInternalReflectionException("field not found");
        } catch (Exception e) {
            throw new LSDInternalReflectionException("unable to reflect field type " +
                    String.valueOf(fieldType) + "#" + n + " for " + String.valueOf(objClass), e);
        }
    }

    /**
     * Convenience wrapper for reflecting a field, eliminating the checked
     * exceptions.
     */
    public static Object getFieldValue(Field field, Object obj) {
        try {
            return field.get(obj);
        } catch (Exception e) {
            throw new LSDInternalReflectionException("unable to get field \"" +
                    String.valueOf(field) + "\" for " + String.valueOf(obj), e);
        }
    }

    /**
     * Set a field's value even if it's marked as final.
     */
    public static void setFinalField(Field field, Object obj, Object value) {
        // Via http://stackoverflow.com/questions/3301635
        try {
            field.setAccessible(true);
            Field fieldField = Field.class.getDeclaredField("modifiers");
            fieldField.setAccessible(true);
            fieldField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
            field.set(obj, value);
        } catch (Exception e) {
            throw new LSDInternalReflectionException("unable to set final field \"" +
                    String.valueOf(field) + "\" for " + String.valueOf(obj), e);
        }
    }
}
