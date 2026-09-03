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
    validateConfiguration();
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

private void validateConfiguration() {

    boolean valid = true;

    int timeout = config.getInt(
            "authentication.login-timeout",
            60
    );

    if (timeout <= 0) {
        plugin.getLogger().severe(
                "Configuration error: authentication.login-timeout " +
                "must be greater than 0."
        );
        valid = false;
    }

    int maxAttempts = config.getInt(
            "authentication.max-login-attempts",
            5
    );

    if (maxAttempts <= 0) {
        plugin.getLogger().severe(
                "Configuration error: authentication.max-login-attempts " +
                "must be greater than 0."
        );
        valid = false;
    }

    int minPassword = config.getInt(
            "authentication.password-min-length",
            8
    );

    int maxPassword = config.getInt(
            "authentication.password-max-length",
            32
    );

    if (minPassword <= 0) {
        plugin.getLogger().severe(
                "Configuration error: authentication.password-min-length " +
                "must be greater than 0."
        );
        valid = false;
    }

    if (maxPassword <= 0) {
        plugin.getLogger().severe(
                "Configuration error: authentication.password-max-length " +
                "must be greater than 0."
        );
        valid = false;
    }

    if (minPassword > maxPassword) {
        plugin.getLogger().severe(
                "Configuration error: authentication.password-min-length " +
                "cannot be greater than authentication.password-max-length."
        );
        valid = false;
    }

    String storageType = config.getString(
            "storage.type",
            "sqlite"
    );

    if (storageType == null ||
            !storageType.equalsIgnoreCase("sqlite")) {

        plugin.getLogger().severe(
                "Configuration error: storage.type must be 'sqlite'."
        );
        valid = false;
    }

    String duration = config.getString(
            "session.duration",
            "3d"
    );

    if (!isValidDuration(duration)) {
        plugin.getLogger().severe(
                "Configuration error: session.duration has invalid value: '" +
                duration + "'."
        );

        plugin.getLogger().severe(
                "Expected a duration such as 30s, 10m, 2h, or 3d."
        );

        valid = false;
    }

    if (valid) {
        plugin.getLogger().info(
                "Configuration validated successfully."
        );
    }
}

private boolean isValidDuration(String duration) {

    if (duration == null || duration.isBlank()) {
        return false;
    }

    String value = duration.trim()
            .toLowerCase(Locale.ROOT);

    char unit = value.charAt(value.length() - 1);

    if (unit != 's' &&
            unit != 'm' &&
            unit != 'h' &&
            unit != 'd') {
        return false;
    }

    String number = value.substring(
            0,
            value.length() - 1
    ).trim();

    try {
        return Long.parseLong(number) > 0;
    } catch (NumberFormatException exception) {
        return false;
    }
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

    public boolean isSessionEnabled() {
        return config.getBoolean(
                "session.enabled",
                true
        );
    }

public long getSessionDuration() {

    String duration = config.getString(
            "session.duration",
            "3d"
    );

    if (!isValidDuration(duration)) {
        return 3L * 24L * 60L * 60L * 1000L;
    }

    String value = duration.trim()
            .toLowerCase(Locale.ROOT);

    long multiplier;

    switch (value.charAt(value.length() - 1)) {
        case 'd' -> multiplier = 24L * 60L * 60L * 1000L;
        case 'h' -> multiplier = 60L * 60L * 1000L;
        case 'm' -> multiplier = 60L * 1000L;
        case 's' -> multiplier = 1000L;
        default -> throw new IllegalStateException(
                "Invalid session duration."
        );
    }

    long amount = Long.parseLong(
            value.substring(0, value.length() - 1).trim()
    );

    return Math.multiplyExact(amount, multiplier);
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
