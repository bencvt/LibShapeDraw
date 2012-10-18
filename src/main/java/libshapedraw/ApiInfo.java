package libshapedraw;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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
    @Deprecated public static String getUrl() {
        return getInstance().urlMain;
    }
    public static String getUrlMain() {
        return getInstance().urlMain;
    }
    public static String getUrlSource() {
        return getInstance().urlSource;
    }
    public static String getUrlUpdate() {
        return getInstance().urlUpdate;
    }
    public static String getAuthors() {
        return getInstance().authors;
    }

    private final String name;
    private final String version;
    private final String urlMain;
    private final String urlSource;
    private final String urlUpdate;
    private final String authors;

    private static ApiInfo instance;

    private ApiInfo() {
        Properties props = new Properties();
        InputStream in = getClass().getResourceAsStream("api.properties");
        try {
            props.load(in);
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("unable to load resource", e);
        }
        name = notNull(props.getProperty("name"));
        version = notNull(props.getProperty("version"));
        urlMain = notNull(props.getProperty("url-main"));
        urlSource = notNull(props.getProperty("url-source"));
        urlUpdate = notNull(props.getProperty("url-update"));
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
}
