package libshapedraw.primitive;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;
import libshapedraw.animation.TestAnimates;

import org.junit.Test;

public class TestVector3 extends SetupTestEnvironment.TestCase {
    private static final double EPSILON = ReadonlyVector3.EPSILON;

    private static void assertVectorEquals(double expectedX, double expectedY, double expectedZ, ReadonlyVector3 v) {
        assertTrue(v.equals(expectedX, expectedY, expectedZ, EPSILON));
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
        assertVectorEquals(0, 0, 0, new Vector3());
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

    @SuppressWarnings("deprecation")
    @Test
    public void testEquals() {
        ReadonlyVector3 v0 = new Vector3(1.0, 2.0, 3.0);

        // Different instances with exactly the same components.
        ReadonlyVector3 v1 = new Vector3(1.0, 2.0, 3.0);
        compareVectors(true, true, v0, v1, EPSILON);

        // Transitive: if A==B and B==C then A==C.
        ReadonlyVector3 v2 = new Vector3(1.0, 2.0, 3.0);
        compareVectors(true, true, v0, v2, EPSILON);
        compareVectors(true, true, v1, v2, EPSILON);

        // Components differ by an amount below the margin of error (epsilon).
        ReadonlyVector3 v3 = v0.copy().addZ(EPSILON/2.0);
        compareVectors(false, true, v0, v3, EPSILON);

        // Components differ by an amount above the margin of error.
        ReadonlyVector3 v4 = new Vector3(-31.214, 21333.7, 867.5309);
        compareVectors(false, false, v0, v4, EPSILON);

        // Same as above, but also try adjusting the margin of error.
        ReadonlyVector3 v5 = new Vector3(1.3, 1.9, 3.2);
        compareVectors(false, false, v0, v5, EPSILON);
        compareVectors(false, true, v0, v5, 0.5);

        // Reflexive: a vector is always exactly equal to itself.
        for (ReadonlyVector3 v : new ReadonlyVector3[] {v0, v1, v2, v3, v4, v5}) {
            compareVectors(true, true, v, v, EPSILON);
            compareVectors(true, true, v, v, 0.5);

            // Comparisons to null are always false.
            compareVectors(false, false, v, null, EPSILON);

            // Comparisons to non-Vector3 objects are always false.
            assertEquals(false, v.equals(42));
            assertEquals(false, v.equals("hello"));
            assertEquals(false, v.equals(new Object()));
        }
    }

    private static void compareVectors(boolean expectedExact, boolean expectedFuzzy, ReadonlyVector3 v, ReadonlyVector3 other, double epsilon) {
        // Consistent: as long as the vectors aren't changed in the meantime,
        // repeated comparisons always return the same value.
        compareVectorsSymmetric(expectedExact, expectedFuzzy, v, other, epsilon);
        compareVectorsSymmetric(expectedExact, expectedFuzzy, v, other, epsilon);
        compareVectorsSymmetric(expectedExact, expectedFuzzy, v, other, epsilon);
    }

    private static void compareVectorsSymmetric(boolean expectedExact, boolean expectedFuzzy, ReadonlyVector3 v, ReadonlyVector3 other, double epsilon) {
        // Symmetric: it doesn't matter what order vectors are compared in.
        compareVectorsWork(expectedExact, expectedFuzzy, v, other, epsilon);
        if (other != null) {
            compareVectorsWork(expectedExact, expectedFuzzy, other, v, epsilon);
        }
    }

    @SuppressWarnings("deprecation")
    private static void compareVectorsWork(boolean expectedExact, boolean expectedFuzzy, ReadonlyVector3 v, ReadonlyVector3 other, double epsilon) {
        assertEquals(expectedExact, v.equals(other));

        assertEquals(expectedExact, v.equalsExact(other));
        assertEquals(expectedFuzzy, v.equals(other, epsilon));
        assertFalse(v.equals(other, -1.0)); // a negative epsilon is pointless but allowed

        if (other != null) {
            assertEquals(expectedExact, v.equalsExact(other.getX(), other.getY(), other.getZ()));
            assertEquals(expectedFuzzy, v.equals(other.getX(), other.getY(), other.getZ(), epsilon));
            assertFalse(v.equals(other.getX(), other.getY(), other.getZ(), -1.0));

            assertEquals(expectedFuzzy, v.componentEquals(Axis.X, other.getX(), epsilon));
            assertEquals(expectedFuzzy, v.componentEquals(Axis.Y, other.getY(), epsilon));
            assertEquals(expectedFuzzy, v.componentEquals(Axis.Z, other.getZ(), epsilon));
        }
    }

    @Test
    public void testHashCode() {
        assertEquals(29791, new Vector3().hashCode());
        assertEquals(29791, Vector3.ZEROS.hashCode());

        assertEquals(66614367, new Vector3(1.0, 2.0, 3.0).hashCode());

        // Different instance, same exact components, same hash code.
        assertEquals(66614367, new Vector3(1.0, 2.0, 3.0).hashCode());

        // Hash code doesn't consider margins of error.
        // It's all based on bits, devoid of semantic meaning.
        assertEquals(-956017950, new Vector3(1.0, 2.0, 3.0).addZ(EPSILON/2.0).hashCode());
    }

    @Test
    public void testIsZero() {
        assertTrue(Vector3.ZEROS.isZero());

        Vector3 v = new Vector3();
        assertTrue(v.isZero());
        v.setY(22.0);
        assertFalse(v.isZero());
        v.set(Vector3.ZEROS);
        assertTrue(v.isZero());
        v.setZ(0.00000001);
        assertFalse(v.isZero());
        v.set(0, 0, 0);
        assertTrue(v.isZero());
    }

    @Test
    public void testLengthSquared() {
        assertEquals(0.0, new Vector3(0, 0, 0).lengthSquared(), EPSILON);
        assertEquals(1.0, new Vector3(1, 0, 0).lengthSquared(), EPSILON);
        assertEquals(1.0, new Vector3(0, 1, 0).lengthSquared(), EPSILON);
        assertEquals(1.0, new Vector3(0, -1, 0).lengthSquared(), EPSILON);
        assertEquals(2.0, new Vector3(0, 1, 1).lengthSquared(), EPSILON);
        assertEquals(4.0, new Vector3(0, 2, 0).lengthSquared(), EPSILON);
        assertEquals(13.0, new Vector3(2, -3, 0).lengthSquared(), EPSILON);
        assertEquals(8088.5625, new Vector3(13.25, 32.0, 83.0).lengthSquared(), EPSILON);
        assertEquals(8088.5625, new Vector3(-13.25, 32.0, -83.0).lengthSquared(), EPSILON);
    }

    @Test
    public void testLength() {
        assertEquals(0.0, new Vector3(0, 0, 0).length(), EPSILON);
        assertEquals(1.0, new Vector3(1, 0, 0).length(), EPSILON);
        assertEquals(1.0, new Vector3(0, 1, 0).length(), EPSILON);
        assertEquals(1.0, new Vector3(0, -1, 0).length(), EPSILON);
        assertEquals(Math.sqrt(2.0), new Vector3(0, 1, 1).length(), EPSILON);
        assertEquals(2.0, new Vector3(0, 2, 0).length(), EPSILON);
        assertEquals(Math.sqrt(13.0), new Vector3(2, -3, 0).length(), EPSILON);
        assertEquals(Math.sqrt(8088.5625), new Vector3(13.25, 32.0, 83.0).length(), EPSILON);
        assertEquals(Math.sqrt(8088.5625), new Vector3(-13.25, 32.0, -83.0).length(), EPSILON);
    }

    @Test
    public void testDistance() {
        ReadonlyVector3 v0 = new Vector3(13.25, 32.0, 83.0);
        ReadonlyVector3 v1 = new Vector3(21.0, 55.5, -213.0);
        // distance from self is 0
        assertEquals(0.0, v0.distanceSquared(v0), EPSILON);
        assertEquals(0.0, v0.distance(v0), EPSILON);
        // simple
        assertEquals(12.25, v0.distanceSquared(v0.copy().addZ(3.5)), EPSILON);
        assertEquals(3.5, v0.distance(v0.copy().addZ(3.5)), EPSILON);
        // complex
        assertEquals(88228.3125, v0.distanceSquared(v1), EPSILON);
        assertEquals(Math.sqrt(88228.3125), v0.distance(v1), EPSILON);
        // commutative
        assertEquals(v0.distanceSquared(v1), v1.distanceSquared(v0), EPSILON);
        assertEquals(v0.distance(v1), v1.distance(v0), EPSILON);
        // overflow
        final double B = Double.MAX_VALUE / 2;
        ReadonlyVector3 big0 = new Vector3(-B, -B, -B);
        ReadonlyVector3 big1 = new Vector3(B, B, B);
        assertEquals(Double.POSITIVE_INFINITY, big0.distanceSquared(big1), EPSILON);
        assertEquals(Double.POSITIVE_INFINITY, big0.distance(big1), EPSILON);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testDistanceDeprecated() {
        ReadonlyVector3 v0 = new Vector3(13.25, 32.0, 83.0);
        ReadonlyVector3 v1 = new Vector3(21.0, 55.5, -213.0);
        // distance from self is 0
        assertEquals(0.0, v0.getDistanceSquared(v0), EPSILON);
        assertEquals(0.0, v0.getDistance(v0), EPSILON);
        // simple
        assertEquals(12.25, v0.getDistanceSquared(v0.copy().addZ(3.5)), EPSILON);
        assertEquals(3.5, v0.getDistance(v0.copy().addZ(3.5)), EPSILON);
        // complex
        assertEquals(88228.3125, v0.getDistanceSquared(v1), EPSILON);
        assertEquals(Math.sqrt(88228.3125), v0.getDistance(v1), EPSILON);
        // commutative
        assertEquals(v0.getDistanceSquared(v1), v1.getDistanceSquared(v0), EPSILON);
        assertEquals(v0.getDistance(v1), v1.getDistance(v0), EPSILON);
        // overflow
        final double B = Double.MAX_VALUE / 2;
        ReadonlyVector3 big0 = new Vector3(-B, -B, -B);
        ReadonlyVector3 big1 = new Vector3(B, B, B);
        assertEquals(Double.POSITIVE_INFINITY, big0.getDistanceSquared(big1), EPSILON);
        assertEquals(Double.POSITIVE_INFINITY, big0.getDistance(big1), EPSILON);
    }

    @Test
    public void testDot() {
        ReadonlyVector3 v0 = new Vector3(13.25, 32.0, 83.0);
        ReadonlyVector3 v1 = new Vector3(21.0, 55.5, -213.0);

        assertEquals(0.0, v0.dot(Vector3.ZEROS), EPSILON);
        assertEquals(0.0, Vector3.ZEROS.dot(v0), EPSILON);
        assertEquals(0.0, Vector3.ZEROS.dot(Vector3.ZEROS), EPSILON);

        assertEquals(0.0, new Vector3(0, 0, 1).dot(new Vector3(0, 1, 0)), EPSILON);
        assertEquals(1.0, new Vector3(0, 0, 1).dot(new Vector3(0, 0, 1)), EPSILON);

        assertEquals(-15624.75, v0.dot(v1), EPSILON);
        assertEquals(-15624.75, v1.dot(v0), EPSILON);

        assertEquals(8088.5625, v0.dot(v0), EPSILON);
        assertEquals(48890.25, v1.dot(v1), EPSILON);
    }

    @Test
    public void testAngle() {
        ReadonlyVector3 v000 = new Vector3(0, 0, 0);
        ReadonlyVector3 v001 = new Vector3(0, 0, 1);
        ReadonlyVector3 v010 = new Vector3(0, 1, 0);
        ReadonlyVector3 v101 = new Vector3(1, 0, 1);
        ReadonlyVector3 v111 = new Vector3(1, 1, 1);
        ReadonlyVector3 vA = new Vector3(13.25, 32.0, 83.0);
        ReadonlyVector3 vB = new Vector3(21.0, 55.5, -213.0);

        // if either vector is all zeros, the angle is zero
        assertAngle(0.0, 0.0, vA, v000);
        assertAngle(0.0, 0.0, v000, v000);

        // angle between a non-zero vector and itself (or any vector pointing
        // in the same direction) is always zero
        assertAngle(0.0, 0.0, v010, v010);
        assertAngle(0.0, 0.0, v111, v111);
        assertAngle(0.0, 0.0, vA, vA);
        assertAngle(0.0, 0.0, vB, vB);
        assertAngle(0.0, 0.0, vB, vB.copy().scale(123.456));

        // opposite directions
        assertAngle(Math.PI, 180.0, v010, v010.copy().negate());
        assertAngle(Math.PI, 180.0, v111, v111.copy().negate());
        assertAngle(Math.PI, 180.0, vB, vB.copy().negate());

        // right angles
        assertAngle(Math.PI / 2, 90.0, v001, v010);
        assertAngle(Math.PI / 2, 90.0, v010, v101);

        // arbitrary angles
        assertAngle(2.4746510644677144, 141.7870617615563, vA, vB);
    }
    private static void assertAngle(double expectedRadians, double expectedDegrees, ReadonlyVector3 v0, ReadonlyVector3 v1) {
        assertEquals(expectedDegrees, v0.angleDegrees(v1), EPSILON);
        assertEquals(expectedDegrees, v1.angleDegrees(v0), EPSILON);
        assertEquals(expectedRadians, v0.angle(v1), EPSILON);
        assertEquals(expectedRadians, v1.angle(v0), EPSILON);
    }

    @Test
    public void testYaw() {
        // any vector without either x or z components has a yaw of 0
        assertYaw(0.0, 0.0, Vector3.ZEROS);
        assertYaw(0.0, 0.0, new Vector3(0, 1, 0));
        assertYaw(0.0, 0.0, new Vector3(0, -1, 0));
        assertYaw(0.0, 0.0, new Vector3(0, 123.456, 0));

        // the y component is not a factor
        for (double y : new double[] {0.0, 1.0, -1.0, 123.456}) {
            // nor is the magnitude of the vector
            for (double scale : new double[] {1.0, 0.1, 867.5309}) {
                // go around the circle
                assertYaw(Math.PI *  0 / 4,    0.0, new Vector3( 0, y,  1).scale(scale));
                assertYaw(Math.PI *  1 / 4,   45.0, new Vector3( 1, y,  1).scale(scale));
                assertYaw(Math.PI *  2 / 4,   90.0, new Vector3( 1, y,  0).scale(scale));
                assertYaw(Math.PI *  3 / 4,  135.0, new Vector3( 1, y, -1).scale(scale));
                assertYaw(Math.PI *  4 / 4,  180.0, new Vector3( 0, y, -1).scale(scale));
                assertYaw(Math.PI * -3 / 4, -135.0, new Vector3(-1, y, -1).scale(scale));
                assertYaw(Math.PI * -2 / 4,  -90.0, new Vector3(-1, y,  0).scale(scale));
                assertYaw(Math.PI * -1 / 4,  -45.0, new Vector3(-1, y,  1).scale(scale));
            }
        }
    }
    private static void assertYaw(double expectedRadians, double expectedDegrees, ReadonlyVector3 v) {
        assertEquals(expectedDegrees, v.yawDegrees(), EPSILON);
        assertEquals(expectedRadians, v.yaw(), EPSILON);
    }

    @Test
    public void testPitch() {
        assertPitch(0.0, 0.0, Vector3.ZEROS);

        assertPitch(-Math.PI/2, -90.0, new Vector3(0, -1, 0));
        assertPitch(Math.PI/2, 90.0, new Vector3(0, 1, 0));
        assertPitch(Math.PI/2, 90.0, new Vector3(0, 123.456, 0));

        assertPitch(-Math.PI/4, -45.0, new Vector3(1, -1, 1));
        assertPitch(0.0, 0.0, new Vector3(1, 0, 1));
        assertPitch(Math.PI/4, 45.0, new Vector3(1, 1, 1));
    }
    private static void assertPitch(double expectedRadians, double expectedDegrees, ReadonlyVector3 v) {
        assertEquals(expectedDegrees, v.pitchDegrees(), EPSILON);
        assertEquals(expectedRadians, v.pitch(), EPSILON);
    }

    @Test
    public void testIsInAABB() {
        ReadonlyVector3 vA = new Vector3(3.5, -23.0, 1324.0);
        ReadonlyVector3 vB = new Vector3(17.0, 42.5, 1872.0);

        assertTrue(new Vector3(5.0, 0.0, 1522.0).isInAABB(vA, vB));
        assertTrue(new Vector3(5.0, -23.0, 1522.0).isInAABB(vA, vB));
        assertTrue(vA.isInAABB(vA, vB));
        assertTrue(vB.isInAABB(vA, vB));

        assertFalse(new Vector3(0.0, 0.0, 1522.0).isInAABB(vA, vB));
        assertFalse(new Vector3(5.0, -23.0, 2522.0).isInAABB(vA, vB));

        assertTrue(Vector3.ZEROS.isInAABB(new Vector3(-1,-1,-1), new Vector3(1,1,1)));
        // still works even if the corners are specified incorrectly
        assertTrue(Vector3.ZEROS.isInAABB(new Vector3(1,1,1), new Vector3(-1,-1,-1)));
        assertTrue(Vector3.ZEROS.isInAABB(new Vector3(1,-1,-1), new Vector3(-1,1,1)));
        assertTrue(Vector3.ZEROS.isInAABB(new Vector3(-1,1,-1), new Vector3(1,-1,1)));
        assertTrue(Vector3.ZEROS.isInAABB(new Vector3(-1,-1,1), new Vector3(1,1,-1)));
    }

    @Test
    public void testIsInSphere() {
        ReadonlyVector3 v = new Vector3(3.5, -23.0, 1324.0);

        for (double radius : new double[] {1.0, 0.01, 20.5, -20.5, 0.0}) {
            assertTrue(Vector3.ZEROS.isInSphere(Vector3.ZEROS, radius));
            assertTrue(v.isInSphere(v, radius));
        }

        assertTrue(v.isInSphere(v.copy().addX(5.0), 6.0));
        assertTrue(v.isInSphere(v.copy().addX(5.0), 5.0));
        assertFalse(v.isInSphere(v.copy().addX(5.0), 4.0));
        assertFalse(v.isInSphere(v.copy().addX(5.0), -4.0));
        assertTrue(v.isInSphere(v.copy().addX(5.0), -5.0));
        assertTrue(v.isInSphere(v.copy().addX(5.0), -6.0));
    }

    @Test
    public void testGlApply() {
        // no exceptions thrown
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        v.glApplyRotateDegrees(45);
        v.glApplyRotateDegrees(-22.5);
        v.glApplyRotateDegrees(9001);
        v.glApplyRotateRadians(Math.PI);
        v.glApplyScale();
        v.glApplyTranslate();
    }

    @Test
    public void testToString() {
        assertEquals("(1.0,2.0,3.0)", new Vector3(1.0, 2.0, 3.0).toString());
        assertEquals("(-531.25,25.0,2312.0)", new Vector3(-531.25, 25.0, 2312.0).toString());
    }

    @Test
    public void testSerializable() {
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        new TestSerializable<ReadonlyVector3>().assertSerializable(v);
        v.animateStartLoop(v, true, 2000);
        new TestSerializable<ReadonlyVector3>().assertSerializable(v);
        assertTrue(v.isAnimating());
        v.animateStop();
    }

    // ========
    // Mutators
    // ========

    @Test
    public void testSet() {
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        v.set(-4.0, 55.55, 62.0);
        assertVectorEquals(-4.0, 55.55, 62.0, v);
        v.set(new Vector3(5.4, 2.3, -77.7));
        assertVectorEquals(5.4, 2.3, -77.7, v);
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
        v.setComponent(Axis.Y, -5.0).setComponent(Axis.X, 0.25).setComponent(Axis.Z, 42.5);
        assertVectorEquals(0.25, -5.0, 42.5, v);
    }

    @Test
    public void testSwapComponents() {
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        v.swapComponents();
        assertVectorEquals(3.0, 1.0, 2.0, v);
        v.swapComponents();
        assertVectorEquals(2.0, 3.0, 1.0, v);
        v.swapComponents();
        assertVectorEquals(1.0, 2.0, 3.0, v);
        v.swapComponents().swapComponents().swapComponents();
        assertVectorEquals(1.0, 2.0, 3.0, v);
    }

    @Test
    public void testComponentsNullAxis() {
        final Vector3 v = new Vector3();
        assertThrowsIAE(new Runnable() { @Override public void run() {
            v.getComponent(null);
        }});
        assertThrowsIAE(new Runnable() { @Override public void run() {
            v.setComponent(null, 42);
        }});
        assertThrowsIAE(new Runnable() { @Override public void run() {
            v.addComponent(null, 42);
        }});
        assertThrowsIAE(new Runnable() { @Override public void run() {
            v.scaleComponent(null, 2.5);
        }});
        assertThrowsIAE(new Runnable() { @Override public void run() {
            v.clampComponent(null, -7.5, 20.25);
        }});
    }

    @Test
    public void testAddSubtract() {
        Vector3 v0 = Vector3.ZEROS.copy();

        // add components
        v0.set(1.0, 2.0, 3.0).addX(7.75).addY(8.0).addZ(-60.0);
        assertVectorEquals(8.75, 10.0, -57.0, v0);
        v0.set(1.0, 2.0, 3.0).add(4.0, -21.5, 50.0);
        assertVectorEquals(5.0, -19.5, 53.0, v0);
        v0.set(1.0, 2.0, 3.0).addComponent(Axis.Z, -5.0).addComponent(Axis.X, 0.125).addComponent(Axis.Y, 0.0);
        assertVectorEquals(1.125, 2.0, -2.0, v0);

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
        Vector3 v = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        // 1.0 is a noop
        v.scaleX(1.0).scaleY(1.0).scaleZ(1.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        v.scale(1.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        // -1.0 reverses signs
        v.scale(-1.0);
        assertVectorEquals(-1.0, -2.0, -3.0, v);
        v.scale(-1.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        // scale down
        v.scale(0.5);
        assertVectorEquals(0.5, 1.0, 1.5, v);
        v.scaleX(0.5);
        assertVectorEquals(0.25, 1.0, 1.5, v);
        v.scaleY(0.5);
        assertVectorEquals(0.25, 0.5, 1.5, v);
        v.scaleZ(0.5);
        assertVectorEquals(0.25, 0.5, 0.75, v);
        // scale up
        v.scale(4.0);
        assertVectorEquals(1.0, 2.0, 3.0, v);
        v.scale(10000.0);
        assertVectorEquals(10000.0, 20000.0, 30000.0, v);
        // scale 0.0
        v.scale(0.0);
        assertVectorEquals(0.0, 0.0, 0.0, v);
        // individual components
        v.set(10.0, 10.0, 10.0);
        v.scaleComponent(Axis.X, 0.5);
        assertVectorEquals(5.0, 10.0, 10.0, v);
        v.scaleComponent(Axis.Z, 100.0).scaleComponent(Axis.Y, 0.0);
        assertVectorEquals(5.0, 0.0, 1000.0, v);
    }

    @Test
    public void testZero() {
        assertVectorEquals(0.0, 0.0, 0.0, new Vector3(1, 2, 3).zero());
        assertVectorEquals(0.0, 0.0, 0.0, new Vector3(-4, 0, 4).zero());
    }

    @Test
    public void testSetRandom() {
        // no exception thrown
        new Vector3(1, 2, 3).setRandom();
        new Vector3(-4, 0, 4).setRandom();
    }

    @Test
    public void testSetMinMax() {
        ReadonlyVector3 vA = new Vector3(53.5, -6521.0, -32.0);
        ReadonlyVector3 vB = new Vector3(22.0, -322.0, 3.0);
        Vector3 v0 = new Vector3(1.0, 2.0, 3.0);
        assertVectorEquals(22.0, -6521.0, -32.0, v0.setMinimum(vA, vB));
        assertVectorEquals(22.0, -6521.0, -32.0, v0.setMinimum(vB, vA));
        assertVectorEquals(53.5, -322.0, 3.0, v0.setMaximum(vA, vB));
        assertVectorEquals(53.5, -322.0, 3.0, v0.setMaximum(vB, vA));
    }

    @Test
    public void testSetFromYawPitch() {
        Vector3 v = Vector3.ZEROS.copy();

        assertVectorEquals(0, 1, 0, v.setFromYawPitchDegrees(0.0, 90.0));
        assertVectorEquals(0, -1, 0, v.setFromYawPitchDegrees(0.0, -90.0));
        assertVectorEquals(0, -1, 0, v.setFromYawPitchDegrees(0.0, 270.0));

        assertVectorEquals(0, 0, 1, v.setFromYawPitchDegrees(0.0, 0.0));
        assertVectorEquals(0, 0, 1, v.setFromYawPitchDegrees(0.0, 360.0));
        assertVectorEquals(0, 0, 1, v.setFromYawPitchDegrees(360.0, 0.0));
        assertVectorEquals(0, 0, 1, v.setFromYawPitchDegrees(-360.0, 0.0));
        assertVectorEquals(0, 0, 1, v.setFromYawPitchDegrees(720.0, 0.0));

        assertVectorEquals(1, 0, 0, v.setFromYawPitchDegrees(90.0, 0.0));
        assertVectorEquals(0, 0, -1, v.setFromYawPitchDegrees(180.0, 0.0));
        assertVectorEquals(-1, 0, 0, v.setFromYawPitchDegrees(270.0, 0.0));
        assertVectorEquals(-1, 0, 0, v.setFromYawPitchDegrees(-90.0, 0.0));

        final double D = 0.7071067811865475;
        assertVectorEquals(0, D, D, v.setFromYawPitchDegrees(0.0, 45.0));
        assertVectorEquals(D, D, 0, v.setFromYawPitchDegrees(90.0, 45.0));
        assertVectorEquals(0, D, -D, v.setFromYawPitchDegrees(180.0, 45.0));
    }

    @Test
    public void testAbsolute() {
        assertVectorEquals(1, 2, 3, new Vector3(1, 2, 3).absolute());
        assertVectorEquals(1, 2, 3, new Vector3(1, -2, 3).absolute());
        assertVectorEquals(1, 2, 3, new Vector3(-1, -2, -3).absolute());
        assertVectorEquals(0, 0, 0, Vector3.ZEROS.copy().absolute());
    }

    @Test
    public void testClamp() {
        assertVectorEquals(1.5, 2, 2.5, new Vector3(1, 2, 3).clamp(1.5, 2.5));
        assertVectorEquals(1.5, 2, 3, new Vector3(1, 2, 3).clampX(1.5, 2.5));
        assertVectorEquals(1, 2, 3, new Vector3(1, 2, 3).clampY(1.5, 2.5));
        assertVectorEquals(1, 2, 2.5, new Vector3(1, 2, 3).clampZ(1.5, 2.5));
        assertVectorEquals(1, 2, 4, new Vector3(1, 2, 3).clampComponent(Axis.Z, 4, 10));

        // if the arguments disagree (clampMax < clampMin), clampMin wins
        assertVectorEquals(1.5, 1.5, 1.5, new Vector3(1, 2, 3).clamp(2.5, 1.5));
    }

    @Test
    public void testDropFraction() {
        ReadonlyVector3 v = new Vector3();
        assertVectorEquals(0, 0, 0, v.copy().truncate());
        assertVectorEquals(0, 0, 0, v.copy().floor());
        assertVectorEquals(0, 0, 0, v.copy().ceiling());
        assertVectorEquals(0, 0, 0, v.copy().round());

        v = new Vector3(0.3, -3.3, 23.7);
        assertVectorEquals(0, -3, 23, v.copy().truncate());
        assertVectorEquals(0, -4, 23, v.copy().floor());
        assertVectorEquals(1, -3, 24, v.copy().ceiling());
        assertVectorEquals(0, -3, 24, v.copy().round());

        v = new Vector3(-0.5, 0.5, -3.96875);
        assertVectorEquals( 0, 0, -3, v.copy().truncate());
        assertVectorEquals(-1, 0, -4, v.copy().floor());
        assertVectorEquals( 0, 1, -3, v.copy().ceiling());
        // Always round up if exactly in between two whole numbers, even if negative.
        assertVectorEquals( 0, 1, -4, v.copy().round());
    }

    @Test
    public void testInterpolate() {
        ReadonlyVector3 target = new Vector3(1, 2, 3);
        assertVectorEquals(6, 5, 4, new Vector3(6, 5, 4).interpolate(target, 0.0));
        assertVectorEquals(1, 2, 3, new Vector3(6, 5, 4).interpolate(target, 1.0));
        assertVectorEquals(3.5, 3.5, 3.5, new Vector3(6, 5, 4).interpolate(target, 0.5));
        assertVectorEquals(3.5, 3.5, 3.5, new Vector3(6, 5, 4).midpoint(target));
        assertVectorEquals(4.75, 4.25, 3.75, new Vector3(6, 5, 4).interpolate(target, 0.25));
        assertVectorEquals(-4, -1, 2, new Vector3(6, 5, 4).interpolate(target, 2.0));
        assertVectorEquals(-44, -25, -6, new Vector3(6, 5, 4).interpolate(target, 10.0));
        assertVectorEquals(11, 8, 5, new Vector3(6, 5, 4).interpolate(target, -1.0));
    }

    @Test
    public void testCross() {
        ReadonlyVector3 v001 = new Vector3(0, 0, 1);
        ReadonlyVector3 v010 = new Vector3(0, 1, 0);
        ReadonlyVector3 v101 = new Vector3(1, 0, 1);
        ReadonlyVector3 vA = new Vector3(13.25, 32.0, 83.0);
        ReadonlyVector3 vB = new Vector3(21.0, 55.5, -213.0);
        Vector3 v = Vector3.ZEROS.copy();

        assertVectorEquals(0, 0, 0, v.zero().cross(Vector3.ZEROS));
        assertVectorEquals(0, 0, 0, v.zero().cross(vA));
        assertVectorEquals(0, 0, 0, v.set(vA).cross(Vector3.ZEROS));

        assertVectorEquals(-1, 0, 0, v.set(v001).cross(v010));
        assertVectorEquals(1, 0, 0, v.set(v010).cross(v001));

        assertVectorEquals(0, 1, 0, v.set(v001).cross(v101));
        assertVectorEquals(0, -1, 0, v.set(v101).cross(v001));

        assertVectorEquals(-11422.5, 4565.25, 63.375, v.set(vA).cross(vB));
        assertVectorEquals(11422.5, -4565.25, -63.375, v.set(vB).cross(vA));
    }

    @Test
    public void testNormalize() {
        assertVectorEquals(0, 0, 0, new Vector3(0, 0, 0).normalize());

        for (double i : new double[] {1.0, 0.1, 0.000123, 123.456}) {
            assertVectorEquals(1, 0, 0, new Vector3(i, 0, 0).normalize());
            assertVectorEquals(0, 1, 0, new Vector3(0, i, 0).normalize());
            assertVectorEquals(0, 0, 1, new Vector3(0, 0, i).normalize());
            assertVectorEquals(-1, 0, 0, new Vector3(-i, 0, 0).normalize());
            assertVectorEquals(0, -1, 0, new Vector3(0, -i, 0).normalize());
            assertVectorEquals(0, 0, -1, new Vector3(0, 0, -i).normalize());
        }

        assertVectorEquals(
                0.8390263620915781,
                0.536044620225175,
                -0.09322515134350869,
                new Vector3(31.5, 20.125, -3.5).normalize());

        assertEquals(1.0, new Vector3(31.5, 20.125, -3.5).normalize().length(), EPSILON);
        assertEquals(1.0, new Vector3(-77.7, 0.0, 9.1).normalize().length(), EPSILON);
    }

    @Test
    public void testAnimate() {
        final Vector3 v0 = new Vector3(1.0, 2.0, 3.0);
        final Vector3 v1 = new Vector3(867, -53.09, 9);
        new TestAnimates<ReadonlyVector3>().assertAnimatesValid(v0, v1, new Vector3(7.0, 8.0, -9.0));

        // animating to self is pointless but allowed
        v0.animateStartLoop(v0, false, 10000);

        // convenience methods
        v0.animateStart(0.0, 0.0, 0.0, 5000);
        assertTrue(v0.isAnimating());
        v0.animateStartLoop(0.5, 1.0, 1.0, true, 5000);
        assertTrue(v0.isAnimating());
        v0.animateStop();
        assertFalse(v0.isAnimating());

        // Copying a vector takes a "snapshot"; the animation does NOT carry over.
        v0.animateStart(new Vector3(7.0, 8.0, -9.0), 750);
        assertTrue(v0.isAnimating());
        assertFalse(v0.copy().isAnimating());
        assertTrue(v0.isAnimating());
        v0.animateStop();
    }
}
