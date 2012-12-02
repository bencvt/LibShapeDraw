package libshapedraw.animation;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;

public class TestAnimates<T> extends SetupTestEnvironment.TestCase {
    // This is not a test case.
    // This method is invoked by other (non-generic) test cases.
    public void assertAnimatesValid(final Animates<T> a0, final Animates<T> a1, final T props) {
        assertNotSame(a0, a1);
        assertFalse(a0.isAnimating());
        assertFalse(a1.isAnimating());

        // harmless to stop an animation that doesn't exist
        a0.animateStop();
        assertFalse(a0.isAnimating());
        a0.animateStop().animateStop();
        assertFalse(a0.isAnimating());

        // starting an animation only affects the one instance
        a0.animateStart(props, 5000);
        assertTrue(a0.isAnimating());
        assertFalse(a1.isAnimating());
        
        // there can be multiple animations running at once
        a1.animateStart(props, 5000);
        assertTrue(a0.isAnimating());
        assertTrue(a1.isAnimating());

        // stoppable
        a0.animateStop();
        assertFalse(a0.isAnimating());
        assertTrue(a1.isAnimating());

        // loop animation
        a0.animateStartLoop(props, true, 3000);
        assertTrue(a0.isAnimating());
        a0.animateStop();

        // starting an animation multiple times is allowed; it just overwrites itself
        a0.animateStart(props, 5000);
        a0.animateStart(props, 300);
        a0.animateStop();
        assertFalse(a0.isAnimating());

        // an animation with a duration of 0 is valid; it will just finish on the next tick
        a0.animateStart(props, 0).animateStop();

        // negative durations are not allowed
        assertThrowsIAE(new Runnable() { @Override public void run() {
            a0.animateStart(props, -1);
        }});
        assertThrowsIAE(new Runnable() { @Override public void run() {
            a0.animateStart(props, -9000);
        }});
        assertThrowsIAE(new Runnable() { @Override public void run() {
            a0.animateStartLoop(props, true, -1);
        }});

        // null properties are not allowed
        assertThrowsIAE(new Runnable() { @Override public void run() {
            a0.animateStart(null, 5000);
        }});
        assertThrowsIAE(new Runnable() { @Override public void run() {
            a0.animateStartLoop(null, false, 5000);
        }});

        assertTrue(a1.isAnimating());
        assertFalse(a1.animateStop().isAnimating());
    }
}
