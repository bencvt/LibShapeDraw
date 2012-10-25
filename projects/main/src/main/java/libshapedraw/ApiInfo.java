package libshapedraw;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import libshapedraw.internal.LSDInternalException;

/**
 * Reference class giving basic information about the LibShapeDraw API.
 */
public class ApiInfo {
    public static String getName() {
        return getInstance().name;
    }
    public static String getVersion() {
        return getInstance().version;
    }
    public static boolean isVersionAtLeast(String minVersion) {
        return minVersion.compareTo(getInstance().version) <= 0;
    }
    @Deprecated public static String getUrl() {
        return getInstance().urlMain.toString();
    }
    public static URL getUrlMain() {
        return getInstance().urlMain;
    }
    public static URL getUrlShort() {
        return getInstance().urlShort;
    }
    public static URL getUrlSource() {
        return getInstance().urlSource;
    }
    public static URL getUrlUpdate() {
        return getInstance().urlUpdate;
    }
    public static String getAuthors() {
        return getInstance().authors;
    }

    private final String name;
    private final String version;
    private final URL urlMain;
    private final URL urlShort;
    private final URL urlSource;
    private final URL urlUpdate;
    private final String authors;

    private static ApiInfo instance;

    private ApiInfo() {
        Properties props = new Properties();
        InputStream in = getClass().getResourceAsStream("api.properties");
        try {
            props.load(in);
            in.close();
        } catch (IOException e) {
            throw new LSDInternalException("unable to load resource", e);
        }
        name = notNull(props.getProperty("name"));
        version = notNull(props.getProperty("version"));
        urlMain = validUrl(props.getProperty("url-main"));
        urlShort = validUrl(props.getProperty("url-short"));
        urlSource = validUrl(props.getProperty("url-source"));
        urlUpdate = validUrl(props.getProperty("url-update"));
        authors = notNull(props.getProperty("authors"));
    }

    private static ApiInfo getInstance() {
        if (instance == null) {
            instance = new ApiInfo();
        }
        return instance;
    }

    private String notNull(String value) {
        if (value == null) {
            throw new IllegalArgumentException("value cannot be null");
        }
        return value;
    }

    private URL validUrl(String value) {
        try {
            return new URL(notNull(value));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("invalid url", e);
        }
    }
}
