package libshapedraw;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestApiInfo extends SetupTestEnvironment.TestCase {
    @Test
    public void testStaticMethods() {
        assertEquals("LibShapeDraw", ApiInfo.getName());

        assertFalse(ApiInfo.getVersion().isEmpty());

        assertFalse(ApiInfo.getUrl().isEmpty());
        assertTrue(ApiInfo.getUrl().contains("://"));

        assertFalse(ApiInfo.getAuthors().isEmpty());
    }
}
