package www0abdb.oss.authiva.config;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class AuthivaConfig {

    private final JavaPlugin plugin;

    private FileConfiguration config;

    private Set<String> allowCommands =
            Collections.emptySet();

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

        for (String command :
                config.getStringList(
                        "commands.allowed-before-login"
                )) {

            String normalized = command.trim();

            if (normalized.isEmpty()) {
                continue;
            }

            if (normalized.startsWith("/")) {
                normalized = normalized.substring(1);
            }

            if (caseInsensitiveCommands()) {
                normalized =
                        normalized.toLowerCase(Locale.ROOT);
            }

            int space = normalized.indexOf(' ');

            if (space >= 0) {
                normalized =
                        normalized.substring(0, space);
            }

            commands.add(normalized);
        }

        allowCommands =
                Collections.unmodifiableSet(commands);
    }

    public int getTimeout() {
        return config.getInt(
                "authentication.login-timeout",
                60
        );
    }

    public int getMaxLoginAttempts() {
        return Math.max(
                1,
                config.getInt(
                        "authentication.max-login-attempts",
                        5
                )
        );
    }

    public boolean remindersEnabled() {
        return config.getBoolean(
                "authentication.reminders.enabled",
                true
        );
    }

    public int getReminderInterval() {
        return Math.max(
                1,
                config.getInt(
                        "authentication.reminders.interval",
                        5
                )
        );
    }

    public boolean sendReminderOnJoin() {
        return config.getBoolean(
                "authentication.reminders.send-on-join",
                true
        );
    }

    public boolean autoLoginAfterRegister() {
        return config.getBoolean(
                "authentication.auto-login-after-register",
                true
        );
    }

    public int getMinPasswordLength() {
        return config.getInt(
                "authentication.password-min-length",
                8
        );
    }

    public int getMaxPasswordLength() {
        return config.getInt(
                "authentication.password-max-length",
                32
        );
    }

    public boolean allowLetters() {
        return config.getBoolean(
                "password.allow-letters",
                true
        );
    }

    public boolean allowNumbers() {
        return config.getBoolean(
                "password.allow-numbers",
                true
        );
    }

    public boolean allowSymbols() {
        return config.getBoolean(
                "password.allow-symbols",
                true
        );
    }

    public boolean requireLetter() {
        return config.getBoolean(
                "password.require-letter",
                true
        );
    }

    public boolean requireNumber() {
        return config.getBoolean(
                "password.require-number",
                true
        );
    }

    public boolean requireSymbol() {
        return config.getBoolean(
                "password.require-symbol",
                false
        );
    }

    public List<String> getForbiddenPasswords() {
        return Collections.unmodifiableList(
                config.getStringList(
                        "password.forbidden"
                )
        );
    }

    public boolean forbiddenPasswordCaseInsensitive() {
        return config.getBoolean(
                "password.forbidden-case-insensitive",
                true
        );
    }

    public boolean isForbiddenPassword(String password) {
        for (String forbidden :
                getForbiddenPasswords()) {

            if (forbiddenPasswordCaseInsensitive()) {
                if (password.equalsIgnoreCase(forbidden)) {
                    return true;
                }
            } else if (password.equals(forbidden)) {
                return true;
            }
        }

        return false;
    }

    public boolean isBypassEnabled() {
        return config.getBoolean(
                "bypass.enabled",
                true
        );
    }

    public String getBypassPermission() {
        return config.getString(
                "bypass.permission",
                "authiva.bypass"
        );
    }

    public boolean freezeMovement() {
        return config.getBoolean(
                "protection.freeze-movement",
                true
        );
    }

    public boolean blockInteraction() {
        return config.getBoolean(
                "protection.block-interaction",
                true
        );
    }

    public boolean blockAttacks() {
        return config.getBoolean(
                "protection.block-attacks",
                true
        );
    }

    public boolean preventMobTargeting() {
        return config.getBoolean(
                "protection.prevent-mob-targeting",
                true
        );
    }

    public boolean blockDamage() {
        return config.getBoolean(
                "protection.block-damage",
                true
        );
    }

    public boolean blockItemDrop() {
        return config.getBoolean(
                "protection.block-item-drop",
                true
        );
    }

    public boolean blockItemPickup() {
        return config.getBoolean(
                "protection.block-item-pickup",
                true
        );
    }

    public boolean blockInventory() {
        return config.getBoolean(
                "protection.block-inventory",
                true
        );
    }

    public boolean blockHeldItemChange() {
        return config.getBoolean(
                "protection.block-held-item-change",
                true
        );
    }

    public boolean caseInsensitiveCommands() {
        return config.getBoolean(
                "protection.case-insensitive-commands",
                true
        );
    }

    public boolean isCommandAllowed(String command) {
        String normalized = command.trim();

        if (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (caseInsensitiveCommands()) {
            normalized =
                    normalized.toLowerCase(Locale.ROOT);
        }

        int space = normalized.indexOf(' ');

        if (space >= 0) {
            normalized =
                    normalized.substring(0, space);
        }

        return allowCommands.contains(normalized);
    }

    public boolean allowHelp() {
        return config.getBoolean(
                "commands.allow-help",
                false
        );
    }

    public boolean checkForUpdates() {
        return config.getBoolean(
                "update-checker.enabled",
                true
        );
    }

    public boolean metricsEnabled() {
        return config.getBoolean(
                "metrics.enabled",
                true
        );
    }

    public List<String> getMessages(
            String key,
            String... replacements
    ) {
        String path = "messages." + key;

        List<String> messages =
                config.getStringList(path);

        if (messages.isEmpty()) {
            String single =
                    config.getString(path);

            if (single == null) {
                return Collections.emptyList();
            }

            messages = List.of(single);
        }

        String prefix =
                config.getString(
                        "messages.prefix",
                        ""
                );

        java.util.ArrayList<String> result =
                new java.util.ArrayList<>();

        for (String message : messages) {

            message = message.replace(
                    "{prefix}",
                    prefix
            );

            for (int i = 0;
                 i + 1 < replacements.length;
                 i += 2) {

                message = message.replace(
                        replacements[i],
                        replacements[i + 1]
                );
            }

            result.add(colorize(message));
        }

        return result;
    }

    public String getMessage(
            String key,
            String... replacements
    ) {
        List<String> messages =
                getMessages(key, replacements);

        if (messages.isEmpty()) {
            return key;
        }

        return String.join("\n", messages);
    }

    private String colorize(String message) {
        return ChatColor.translateAlternateColorCodes(
                '&',
                message
        );
    }
}
