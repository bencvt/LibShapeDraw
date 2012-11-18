package libshapedraw.primitive;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;

import org.junit.Test;

public class TestAxis extends SetupTestEnvironment.TestCase {
    @Test
    public void testNext() {
        assertTrue(Axis.X.next() == Axis.Y);
        assertTrue(Axis.Y.next() == Axis.Z);
        assertTrue(Axis.Z.next() == Axis.X);

        assertTrue(Axis.X.next().next() == Axis.Z);
        assertTrue(Axis.Y.next().next() == Axis.X);
        assertTrue(Axis.Z.next().next() == Axis.Y);

        assertTrue(Axis.X.next().next().next() == Axis.X);
        assertTrue(Axis.Y.next().next().next() == Axis.Y);
        assertTrue(Axis.Z.next().next().next() == Axis.Z);
    }
}
