package libshapedraw.internal;

import static org.junit.Assert.*;
import libshapedraw.SetupTestEnvironment;

import org.junit.Test;

public class TestGlobalSettings extends SetupTestEnvironment.TestCase {
    @Test
    public void testGlobalSettingsDefaults() {
        assertTrue(GlobalSettings.isLoggingEnabled());
        assertFalse(GlobalSettings.isLoggingAppend());
        assertEquals(0, GlobalSettings.getLoggingDebugDumpInterval());
    }
}
