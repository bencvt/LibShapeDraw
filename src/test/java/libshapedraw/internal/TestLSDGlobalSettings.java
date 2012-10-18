package libshapedraw.internal;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;

import org.junit.Test;

public class TestLSDGlobalSettings extends SetupTestEnvironment.TestCase {
    @Test
    public void testGlobalSettingsDefaults() {
        assertTrue(LSDGlobalSettings.isLoggingEnabled());
        assertFalse(LSDGlobalSettings.isLoggingAppend());
        assertEquals(0, LSDGlobalSettings.getLoggingDebugDumpInterval());
        // disabled for unit testing
        //assertTrue(LSDGlobalSettings.isUpdateCheckEnabled());
    }
}
