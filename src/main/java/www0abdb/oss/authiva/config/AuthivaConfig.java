package www0abdb.oss.authiva.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.List;

public final class AuthivaConfig {

    private final JavaPlugin plugin;
    private FileConfiguration config;
    private Set<String> allowCommands = Collections.emptySet();

    public AuthivaConfig(JavaPlugin plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();
        rebuildAllowCommands();
    }

    private void rebuildAllowCommands() {
        Set<String> commands = new HashSet<>();

        for (String command : config.getStringList("settings.allowCommands")) {
            String normalized = command.trim();

            if (normalized.isEmpty()) {
                continue;
            }

            if (!normalized.startsWith("/")) {
                normalized = "/" + normalized;
            }

            if (caseInsensitiveCommands()) {
                normalized = normalized.toLowerCase(Locale.ROOT);
            }

            int space = normalized.indexOf(' ');
            if (space >= 0) {
                normalized = normalized.substring(0, space);
            }

            commands.add(normalized);
        }

        allowCommands = Collections.unmodifiableSet(commands);
    }

    public int getTimeout() {
        return config.getInt("settings.timeout", 60);
    }

    public int getMinPasswordLength() {
        return config.getInt("settings.minPasswordLength", 6);
    }

    public int getMaxPasswordLength() {
        return config.getInt("settings.maxPasswordLength", 32);
    }

    public boolean allowLetters() {
        return config.getBoolean("settings.password.allowLetters", true);
    }

    public boolean allowNumbers() {
        return config.getBoolean("settings.password.allowNumbers", true);
    }

    public boolean allowSymbols() {
        return config.getBoolean("settings.password.allowSymbols", true);
    }

    public boolean requireLetter() {
        return config.getBoolean("settings.password.requireLetter", true);
    }

    public boolean requireNumber() {
        return config.getBoolean("settings.password.requireNumber", true);
    }

    public boolean requireSymbol() {
        return config.getBoolean("settings.password.requireSymbol", false);
    }

    public boolean isCommandAllowed(String command) {
        String normalized = command.trim();

        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }

        if (caseInsensitiveCommands()) {
            normalized = normalized.toLowerCase(Locale.ROOT);
        }

        int space = normalized.indexOf(' ');
        if (space >= 0) {
            normalized = normalized.substring(0, space);
        }

        return allowCommands.contains(normalized);
    }

    public List<String> getUnsafePasswords() {
        return Collections.unmodifiableList(
                config.getStringList("settings.unsafePasswords")
        );
    }

    public String getMessage(String key) {
        return colorize(config.getString(
                "messages." + key,
                key
        ));
    }

    public String getMessage(String key, String... replacements) {
        String message = config.getString(
                "messages." + key,
                key
        );

        for (int i = 0; i + 1 < replacements.length; i += 2) {
            message = message.replace(
                    replacements[i],
                    replacements[i + 1]
            );
        }

        return colorize(message);
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public boolean freezeMovement() {
        return config.getBoolean("protection.freezeMovement", true);
    }

    public boolean blockInteraction() {
        return config.getBoolean("protection.blockInteraction", true);
    }

    public boolean blockAttacks() {
        return config.getBoolean("protection.blockAttacks", true);
    }

    public boolean preventMobTargeting() {
        return config.getBoolean("protection.preventMobTargeting", true);
    }

    public boolean caseInsensitiveCommands() {
        return config.getBoolean(
                "protection.caseInsensitiveCommands",
                true
        );
    }
}
