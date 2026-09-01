package www0abdb.oss.authiva.update;

import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UpdateChecker {

    private static final String API_URL =
            "https://api.github.com/repos/www0abdb-oss/Authiva/releases/latest";

    private static final String RELEASES_URL =
            "https://github.com/www0abdb-oss/Authiva/releases";

    private static final Pattern TAG_PATTERN =
            Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");

    private final JavaPlugin plugin;
    private final String currentVersion;

    public UpdateChecker(JavaPlugin plugin) {
        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
    }

    public void check() {
        plugin.getLogger().info("Checking for updates...");

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/vnd.github+json")
                .header("User-Agent", "Authiva-UpdateChecker")
                .GET()
                .build();

        client.sendAsync(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                )
                .thenAccept(response -> {
                    if (response.statusCode() != 200) {
                        plugin.getLogger().warning(
                                "Unable to check for updates. "
                                        + "GitHub returned HTTP "
                                        + response.statusCode() + "."
                        );
                        return;
                    }

                    handleResponse(response.body());
                })
                .exceptionally(exception -> {
                    plugin.getLogger().warning(
                            "Unable to check for updates: "
                                    + exception.getClass().getSimpleName()
                    );
                    return null;
                });
    }

    private void handleResponse(String response) {
        Matcher matcher = TAG_PATTERN.matcher(response);

        if (!matcher.find()) {
            plugin.getLogger().warning(
                    "Unable to determine the latest Authiva version."
            );
            return;
        }

        String latestVersion = normalize(matcher.group(1));
        String installedVersion = normalize(currentVersion);

        int comparison = compareVersions(
                installedVersion,
                latestVersion
        );

        if (comparison < 0) {
            plugin.getLogger().warning(
                    "A new Authiva version is available: "
                            + latestVersion
                            + " (current: "
                            + currentVersion
                            + ")"
            );

            plugin.getLogger().warning(
                    "Download: " + RELEASES_URL
            );
        } else {
            plugin.getLogger().info(
                    "Authiva is up to date."
            );
        }
    }

    private static String normalize(String version) {
        String normalized = version.trim();

        if (normalized.startsWith("v")
                || normalized.startsWith("V")) {
            normalized = normalized.substring(1);
        }

        int dash = normalized.indexOf('-');

        if (dash >= 0) {
            normalized = normalized.substring(0, dash);
        }

        return normalized;
    }

    private static int compareVersions(
            String current,
            String latest
    ) {
        String[] currentParts = current.split("\\.");
        String[] latestParts = latest.split("\\.");

        int length = Math.max(
                currentParts.length,
                latestParts.length
        );

        for (int i = 0; i < length; i++) {
            int currentPart = i < currentParts.length
                    ? parseNumber(currentParts[i])
                    : 0;

            int latestPart = i < latestParts.length
                    ? parseNumber(latestParts[i])
                    : 0;

            if (currentPart < latestPart) {
                return -1;
            }

            if (currentPart > latestPart) {
                return 1;
            }
        }

        return 0;
    }

    private static int parseNumber(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }
}
