package libshapedraw.animation;

import org.junit.Test;

import libshapedraw.SetupTestEnvironment;

public class TestAnimatedValue extends SetupTestEnvironment.TestCase {
    @Test
    public void testDouble() {
        final AnimatedValue<Double> v0 = new AnimatedValue<Double>(0.0);
        final AnimatedValue<Double> v1 = new AnimatedValue<Double>(60.5);
        new TestAnimates<Double>().assertAnimatesValid(v0, v1, 32.25);
        // animating to self is pointless but allowed
        v0.animateStartLoop(v0.getValue(), false, 10000);
    }

    @Test
    public void testFloat() {
        final AnimatedValue<Float> v0 = new AnimatedValue<Float>(0.0F);
        final AnimatedValue<Float> v1 = new AnimatedValue<Float>(60.5F);
        new TestAnimates<Float>().assertAnimatesValid(v0, v1, 32.25F);
        // animating to self is pointless but allowed
        v0.animateStartLoop(v0.getValue(), false, 10000);
    }

    @Test
    public void testInteger() {
        final AnimatedValue<Integer> v0 = new AnimatedValue<Integer>(0);
        final AnimatedValue<Integer> v1 = new AnimatedValue<Integer>(456);
        new TestAnimates<Integer>().assertAnimatesValid(v0, v1, 32);
        // animating to self is pointless but allowed
        v0.animateStartLoop(v0.getValue(), false, 10000);
    }

    @Test
    public void testLong() {
        final AnimatedValue<Long> v0 = new AnimatedValue<Long>(0L);
        final AnimatedValue<Long> v1 = new AnimatedValue<Long>(99999999999L);
        new TestAnimates<Long>().assertAnimatesValid(v0, v1, 32L);
        // animating to self is pointless but allowed
        v0.animateStartLoop(v0.getValue(), false, 10000);
    }
}
