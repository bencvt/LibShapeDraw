package libshapedraw.primitive;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import libshapedraw.SetupTestEnvironment;
import libshapedraw.animation.Animates;

public class TestSerializable<T extends Serializable> extends SetupTestEnvironment.TestCase {
    // This is not a test case.
    // This method is invoked by other (non-generic) test cases.
    @SuppressWarnings("rawtypes")
    public void assertSerializable(final T a) {
        assertNotNull(a);
        try {
            boolean wasAnimating = false;
            if (a instanceof Animates) {
                wasAnimating = ((Animates) a).isAnimating();
            }
            String fileName = a.getClass().getName() + "-" + getUniqueId() + ".ser";
            write(a, fileName);
            T b = read(fileName);
            assertNotSame(a, b);
            assertTrue(a.equals(b));
            assertTrue(b.equals(a));
            if (a instanceof Animates) {
                assertFalse(((Animates) b).isAnimating());
                assertEquals(wasAnimating, ((Animates) a).isAnimating());
            }
        } catch(Exception e) {
            throw new RuntimeException("unable to serialize");
        }
    }

    private static int topId = 0;
    private synchronized String getUniqueId() {
        topId++;
        return Integer.toString(topId);
    }

    private void write(T obj, String fileName) throws IOException {
        FileOutputStream outFile = new FileOutputStream(new File(getTempDirectory(), fileName));
        ObjectOutputStream outObj = new ObjectOutputStream(outFile);
        outObj.writeObject(obj);
        outObj.close();
        outFile.close();
    }

    @SuppressWarnings("unchecked")
    private T read(String fileName) throws IOException, ClassNotFoundException {
        FileInputStream inFile = new FileInputStream(new File(getTempDirectory(), fileName));
        ObjectInputStream inObj = new ObjectInputStream(inFile);
        T result = (T) inObj.readObject();
        inObj.close();
        inFile.close();
        return result;
    }
}
