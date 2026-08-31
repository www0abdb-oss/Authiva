package www0abdb.oss.authiva.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import www0abdb.oss.authiva.auth.AuthService;
import www0abdb.oss.authiva.config.AuthivaConfig;

public final class ChangePasswordCommand implements CommandExecutor {

    private final AuthService authService;
    private final JavaPlugin plugin;
    private final AuthivaConfig config;

    public ChangePasswordCommand(
            AuthService authService,
            JavaPlugin plugin,
            AuthivaConfig config
    ) {
        this.authService = authService;
        this.plugin = plugin;
        this.config = config;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(config.getMessage("onlyPlayers"));
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(config.getMessage("changePasswordUsage"));
            return true;
        }

        String currentPassword = args[0];
        String newPassword = args[1];

        int min = config.getMinPasswordLength();
        int max = config.getMaxPasswordLength();

        if (newPassword.length() < min) {
            player.sendMessage(
                    config.getMessage(
                            "passwordTooShort",
                            "{min}",
                            String.valueOf(min)
                    )
            );
            return true;
        }

        if (newPassword.length() > max) {
            player.sendMessage(
                    config.getMessage(
                            "passwordTooLong",
                            "{max}",
                            String.valueOf(max)
                    )
            );
            return true;
        }

        if (!config.allowLetters()
                && newPassword.chars().anyMatch(Character::isLetter)) {
            player.sendMessage(config.getMessage("invalidPasswordCharacters"));
            return true;
        }

        if (!config.allowNumbers()
                && newPassword.chars().anyMatch(Character::isDigit)) {
            player.sendMessage(config.getMessage("invalidPasswordCharacters"));
            return true;
        }

        if (!config.allowSymbols()
                && newPassword.chars().anyMatch(
                        c -> !Character.isLetterOrDigit(c))) {
            player.sendMessage(config.getMessage("invalidPasswordCharacters"));
            return true;
        }

        if (config.requireLetter()
                && newPassword.chars().noneMatch(Character::isLetter)) {
            player.sendMessage(config.getMessage("passwordNeedsLetter"));
            return true;
        }

        if (config.requireNumber()
                && newPassword.chars().noneMatch(Character::isDigit)) {
            player.sendMessage(config.getMessage("passwordNeedsNumber"));
            return true;
        }

        if (config.requireSymbol()
                && newPassword.chars().allMatch(Character::isLetterOrDigit)) {
            player.sendMessage(config.getMessage("passwordNeedsSymbol"));
            return true;
        }

        for (String unsafe : config.getUnsafePasswords()) {
            if (newPassword.equalsIgnoreCase(unsafe)) {
                player.sendMessage(config.getMessage("unsafePassword"));
                return true;
            }
        }

        authService.changePasswordAsync(
                player.getUniqueId(),
                currentPassword,
                newPassword,
                result -> plugin.getServer().getScheduler().runTask(
                        plugin,
                        () -> {
                            switch (result) {
                                case SUCCESS ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "passwordChanged"
                                                )
                                        );

                                case NOT_REGISTERED ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "notRegistered"
                                                )
                                        );

                                case INVALID_CURRENT_PASSWORD ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "invalidCurrentPassword"
                                                )
                                        );

                                case FAILED ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "changePasswordFailed"
                                                )
                                        );
                            }
                        }
                ),
                exception -> plugin.getServer().getScheduler().runTask(
                        plugin,
                        () -> {
                            player.sendMessage(
                                    config.getMessage(
                                            "changePasswordFailed"
                                    )
                            );
                            exception.printStackTrace();
                        }
                )
        );

        return true;
    }
}
