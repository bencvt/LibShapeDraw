package libshapedraw.internal;

import libshapedraw.ApiInfo;
import libshapedraw.MinecraftAccess;

/**
 * Internal class. Check the remote website for updates.
 * <p>
 * The server-side part of this is dead simple: a static text file hosted on a
 * web server.
 */
public class LSDUpdateCheck {
    public class UrlRetriever implements Runnable {
        @Override
        public void run() {
            LSDController.getLog().info("update check request: " + ApiInfo.getUrlUpdate());
            String response = LSDUtil.getUrlContents(ApiInfo.getUrlUpdate());
            LSDController.getLog().info("update check response: " + String.valueOf(response));
            if (response == null) {
                return;
            }
            // Parse response and set updateCheckResult, which will be
            // consumed and output later in the main thread.
            String[] lines = response.replaceAll("\t", "  ").split("\n");
            if (lines[0].startsWith("{")) {
                // In case we ever want to switch to JSON in the future
                result = buildOutput("");
                return;
            }
            // The first line is simply the latest published version.
            if (ApiInfo.isVersionAtLeast(lines[0])) {
                return;
            }
            // If the response contains lines of text after the version,
            // that's what we'll output to the user.
            StringBuilder b = new StringBuilder();
            for (int i = 1; i < lines.length; i++) {
                if (!lines[i].isEmpty()) {
                    if (b.length() > 0) {
                        b.append('\n');
                    }
                    b.append(lines[i]);
                }
            }
            if (b.length() > 0) {
                result = b.toString();
            } else {
                // The response was just the version.
                result = buildOutput(lines[0]);
            }
        }

        private String buildOutput(String newVersion) {
            return new StringBuilder().append("\u00a7c")
                    .append(ApiInfo.getName()).append(" is out of date. ")
                    .append(newVersion.isEmpty() ? "A new version" : "Version ")
                    .append(newVersion)
                    .append(" is available at\n  \u00a7c")
                    .append(ApiInfo.getUrlShort()).toString();
        }
    }

    private String result;

    public LSDUpdateCheck() {
        if (LSDGlobalSettings.isUpdateCheckEnabled()) {
            new Thread(new UrlRetriever()).start();
        }
    }

    public void announceResultIfReady(MinecraftAccess minecraftAccess) {
        if (result != null) {
            for (String line : result.split("\n")) {
                minecraftAccess.sendChatMessage(line);
            }
            result = null;
        }
    }

    public String getResult() {
        return result;
    }
}
