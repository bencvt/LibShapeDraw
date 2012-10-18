package libshapedraw.internal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Miscellaneous general-purpose utility classes and methods.
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
            throw new LSDInternalException("ClassLoader.findLoadedClass reflection failed", e);
        }
    }

    /**
     * Create a uniquely-named temporary directory.
     * @see java.nio.file.Files.createTempDirectory
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
     * For getting at those pesky private and obfuscated fields.
     * @return the nth field of the specified type declared by specified class.
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
     * Convenience wrapper for reflecting a field, eliminating the checked exceptions.
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
     * @see http://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
     */
    public static void setFinalField(Field field, Object obj, Object value) {
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
