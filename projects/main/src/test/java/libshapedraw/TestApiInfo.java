package libshapedraw;

import static org.junit.Assert.*;

import java.net.URL;

import libshapedraw.internal.LSDModDirectory;

import org.junit.Test;

public class TestApiInfo extends SetupTestEnvironment.TestCase {
    @SuppressWarnings("deprecation")
    @Test
    public void testStaticMethods() {
        assertEquals("LibShapeDraw", ApiInfo.getName());

        assertFalse(ApiInfo.getVersion().isEmpty());

        assertValidHttpUrl(ApiInfo.getUrlMain());
        assertValidHttpUrl(ApiInfo.getUrlShort());
        assertValidHttpUrl(ApiInfo.getUrlSource());
        assertValidHttpUrl(ApiInfo.getUrlUpdate());

        assertEquals(ApiInfo.getUrlMain().toString(), ApiInfo.getUrl());

        assertFalse(ApiInfo.getAuthors().isEmpty());

        assertEquals(ApiInfo.getModDirectory(), LSDModDirectory.DIRECTORY);
    }

    private static void assertValidHttpUrl(URL url) {
        assertNotNull(url);
        assertFalse(url.toString().isEmpty());
        assertTrue(url.toString().contains("://"));
        assertTrue(url.toString().startsWith("http"));
    }

    @Test
    public void testIsVersionAtLeast() {
        assertTrue(ApiInfo.isVersionAtLeast("0.1"));
        assertTrue(ApiInfo.isVersionAtLeast(ApiInfo.getVersion()));
        assertFalse(ApiInfo.isVersionAtLeast("9999.0"));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testIsVersionAtLeastNull() {
        ApiInfo.isVersionAtLeast(null);
    }
}
