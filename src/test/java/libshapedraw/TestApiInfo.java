package libshapedraw;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestApiInfo extends SetupTestEnvironment.TestCase {
    @SuppressWarnings("deprecation")
    @Test
    public void testStaticMethods() {
        assertEquals("LibShapeDraw", ApiInfo.getName());

        assertFalse(ApiInfo.getVersion().isEmpty());

        assertValidUrl(ApiInfo.getUrlMain());
        assertValidUrl(ApiInfo.getUrlSource());
        assertValidUrl(ApiInfo.getUrlUpdate());

        assertEquals(ApiInfo.getUrlMain(), ApiInfo.getUrl());

        assertFalse(ApiInfo.getAuthors().isEmpty());
    }

    private static void assertValidUrl(String url) {
        assertNotNull(url);
        assertFalse(url.isEmpty());
        assertTrue(url.contains("://"));
    }

    @Test
    public void testIsVersionAtLeast() {
        assertTrue(ApiInfo.isVersionAtLeast("0.1"));
        assertTrue(ApiInfo.isVersionAtLeast(ApiInfo.getVersion()));
        assertFalse(ApiInfo.isVersionAtLeast("9999.0"));
    }

    @Test(expected=NullPointerException.class)
    public void testIsVersionAtLeastNull() {
        ApiInfo.isVersionAtLeast(null);
    }
}
