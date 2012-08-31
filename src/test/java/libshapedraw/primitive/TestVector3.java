package libshapedraw.primitive;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;

import org.junit.Test;

public class TestVector3 extends SetupTestEnvironment.TestCase {
    private static void assertVectorEquals(double expectedX, double expectedY, double expectedZ, ReadonlyVector3 v) {
        assertEquals(expectedX, v.getX(), 0.0);
        assertEquals(expectedY, v.getY(), 0.0);
        assertEquals(expectedZ, v.getZ(), 0.0);
    }

    @Test
    public void testVector() {
        Vector3 v0 = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(1.0, 2.0, 3.0, v0);
        v0.setZ(-888.25);
        assertVectorEquals(1.0, 2.0, -888.25, v0);
        Vector3 v1 = new Vector3(v0);
        assertVectorEquals(1.0, 2.0, -888.25, v1);
        v1.setX(0.0);
        assertVectorEquals(0.0, 2.0, -888.25, v1);
        assertVectorEquals(1.0, 2.0, -888.25, v0);
    }

    @Test(expected=NullPointerException.class)
    public void testConstructorInvalidNull() {
        new Vector3(null);
    }

    @Test
    public void testCopy() {
        Vector3 v0 = new Vector3(1.0, 2.0, 3.0);
        Vector3 v1 = v0.copy();
        assertFalse(v0 == v1);
        assertTrue(v0.getX() == v1.getX());
        assertTrue(v0.getY() == v1.getY());
        assertTrue(v0.getZ() == v1.getZ());
        v0.setX(0.0);
        assertFalse(v0.getX() == v1.getX());
    }

    @Test
    public void testDistance() {
        ReadonlyVector3 v0 = new Vector3(13.25, 32.0, 83.0);
        // distance from self is 0
        assertEquals(0.0, v0.getDistanceSquared(v0), 0.0);
        assertEquals(0.0, v0.getDistance(v0), 0.0);
        // simple
        assertEquals(12.25, v0.getDistanceSquared(v0.copy().addZ(3.5)), 0.0);
        assertEquals(3.5, v0.getDistance(v0.copy().addZ(3.5)), 0.0);
        // complex
        ReadonlyVector3 v1 = new Vector3(21.0, 55.5, -213.0);
        assertEquals(88228.3125, v0.getDistanceSquared(v1), 0.0);
        assertEquals(Math.sqrt(88228.3125), v0.getDistance(v1), 0.0);
        // commutative
        assertEquals(v0.getDistance(v1), v1.getDistance(v0), 0.0);
        assertEquals(v0.getDistanceSquared(v1), v1.getDistanceSquared(v0), 0.0);
    }

    @Test
    public void testSet() {
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        v.set(-4.0, 55.55, 62.0);
        assertVectorEquals(-4.0, 55.55, 62.0, v);
        v.set(new Vector3(5.4, 2.3, -77.7));
        assertVectorEquals(5.4, 2.3, -77.7, v);

        v.set(Vector3.ZEROS);
        assertVectorEquals(0.0, 0.0, 0.0, v);
        v.setY(22.0);
        assertVectorEquals(0.0, 22.0, 0.0, v);
        assertVectorEquals(0.0, 0.0, 0.0, Vector3.ZEROS);
    }

    @Test(expected=NullPointerException.class)
    public void testSetInvalidNull() {
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        v.set(null);
    }

    @Test
    public void testSetComponents() {
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        v.setX(-32.25).setY(78.0).setZ(0.0);
        assertVectorEquals(-32.25, 78.0, 0.0, v);
    }

    @Test
    public void testAddSubtract() {
        Vector3 v0 = Vector3.ZEROS.copy();

        // add components
        v0.set(1.0, 2.0, 3.0).addX(7.75).addY(8.0).addZ(-60.0);
        assertVectorEquals(8.75, 10.0, -57.0, v0);
        v0.set(1.0, 2.0, 3.0).add(4.0, -21.5, 50.0);
        assertVectorEquals(5.0, -19.5, 53.0, v0);

        v0.set(8.75, 10.0, -57.0);
        ReadonlyVector3 v1 = new Vector3(1.0, -2.5, 3.0);
        // add vector
        v0.add(v1);
        assertVectorEquals(9.75, 7.5, -54.0, v0);
        v0.add(v1);
        assertVectorEquals(10.75, 5.0, -51.0, v0);
        // subtract vector
        v0.subtract(v1);
        assertVectorEquals(9.75, 7.5, -54.0, v0);
        v0.subtract(v1);
        assertVectorEquals(8.75, 10.0, -57.0, v0);
        // subtract self
        v0.subtract(v0);
        assertVectorEquals(0.0, 0.0, 0.0, v0);
    }

    @Test
    public void testScale() {
        Vector3 v0 = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(1.0, 2.0, 3.0, v0);
        // 1.0 is a noop
        v0.scaleX(1.0).scaleY(1.0).scaleZ(1.0);
        assertVectorEquals(1.0, 2.0, 3.0, v0);
        v0.scale(1.0);
        assertVectorEquals(1.0, 2.0, 3.0, v0);
        // -1.0 reverses signs
        v0.scale(-1.0);
        assertVectorEquals(-1.0, -2.0, -3.0, v0);
        v0.scale(-1.0);
        assertVectorEquals(1.0, 2.0, 3.0, v0);
        // scale down
        v0.scale(0.5);
        assertVectorEquals(0.5, 1.0, 1.5, v0);
        v0.scaleX(0.5);
        assertVectorEquals(0.25, 1.0, 1.5, v0);
        v0.scaleY(0.5);
        assertVectorEquals(0.25, 0.5, 1.5, v0);
        v0.scaleZ(0.5);
        assertVectorEquals(0.25, 0.5, 0.75, v0);
        // scale up
        v0.scale(4.0);
        assertVectorEquals(1.0, 2.0, 3.0, v0);
        v0.scale(10000.0);
        assertVectorEquals(10000.0, 20000.0, 30000.0, v0);
        // scale 0.0
        v0.scale(0.0);
        assertVectorEquals(0.0, 0.0, 0.0, v0);
    }

    @Test
    public void testToString() {
        assertEquals("(1.0,2.0,3.0)", new Vector3(1.0, 2.0, 3.0).toString());
        assertEquals("(-531.25,25.0,2312.0)", new Vector3(-531.25, 25.0, 2312.0).toString());
    }
}
