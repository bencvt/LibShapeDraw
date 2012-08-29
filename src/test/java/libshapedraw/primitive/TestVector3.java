package libshapedraw.primitive;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.primitive.Vector3;

import org.junit.Test;

public class TestVector3 extends SetupTestEnvironment.TestCase {
    @Test
    public void testVector() {
        Vector3 v0 = new Vector3(1.0, 2.0, 3.0);
        assertTrue(v0.getX() == 1.0 && v0.getY() == 2.0 && v0.getZ() == 3.0);
        v0.setZ(-888.25);
        assertTrue(v0.getX() == 1.0 && v0.getY() == 2.0 && v0.getZ() == -888.25);
        Vector3 v1 = new Vector3(v0);
        assertTrue(v1.getX() == 1.0 && v1.getY() == 2.0 && v1.getZ() == -888.25);
        v1.setX(0.0);
        assertTrue(v0.getX() == 1.0 && v0.getY() == 2.0 && v0.getZ() == -888.25);
        assertTrue(v1.getX() == 0.0 && v1.getY() == 2.0 && v1.getZ() == -888.25);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNull() {
        new Vector3(null);
    }

    @Test
    public void testToString() {
        assertEquals("(1.0,2.0,3.0)", new Vector3(1.0, 2.0, 3.0).toString());
        assertEquals("(-531.25,25.0,2312.0)", new Vector3(-531.25, 25.0, 2312.0).toString());
    }
}
