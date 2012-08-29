package libshapedraw.internal;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
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
public class Util {
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
                throw new RuntimeException("unable to add file handler for " + logFilePattern, e);
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
            throw new RuntimeException("ClassLoader.findLoadedClass reflection failed", e);
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
}
