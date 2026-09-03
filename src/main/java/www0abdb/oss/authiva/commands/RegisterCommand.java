package www0abdb.oss.authiva.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.plugin.java.JavaPlugin;
import www0abdb.oss.authiva.auth.AuthService;
import www0abdb.oss.authiva.config.AuthivaConfig;

public final class RegisterCommand implements CommandExecutor {

    private final AuthService authService;
    private final JavaPlugin plugin;
    private final AuthivaConfig config;

    public RegisterCommand(
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
            sender.sendMessage(
                    config.getMessage("onlyPlayers")
            );
            return true;
        }

        if (args.length != 2) {
            player.sendMessage(
                    config.getMessage("registerUsage")
            );
            return true;
        }

        String password = args[0];
        String confirmPassword = args[1];

        if (!password.equals(confirmPassword)) {
            player.sendMessage(
                    config.getMessage("passwordMismatch")
            );
            return true;
        }

        int min = config.getMinPasswordLength();
        int max = config.getMaxPasswordLength();

        if (password.length() < min) {
            player.sendMessage(
                    config.getMessage(
                            "passwordTooShort",
                            "{min}", String.valueOf(min)
                    )
            );
            return true;
        }

        if (password.length() > max) {
            player.sendMessage(
                    config.getMessage(
                            "passwordTooLong",
                            "{max}", String.valueOf(max)
                    )
            );
            return true;
        }

        if (!config.allowLetters()
                && password.chars().anyMatch(Character::isLetter)) {
            player.sendMessage(
                    config.getMessage("invalidPasswordCharacters")
            );
            return true;
        }

        if (!config.allowNumbers()
                && password.chars().anyMatch(Character::isDigit)) {
            player.sendMessage(
                    config.getMessage("invalidPasswordCharacters")
            );
            return true;
        }

        if (!config.allowSymbols()
                && password.chars().anyMatch(
                        c -> !Character.isLetterOrDigit(c))) {
            player.sendMessage(
                    config.getMessage("invalidPasswordCharacters")
            );
            return true;
        }

        if (config.requireLetter()
                && password.chars().noneMatch(Character::isLetter)) {
            player.sendMessage(
                    config.getMessage("passwordNeedsLetter")
            );
            return true;
        }

        if (config.requireNumber()
                && password.chars().noneMatch(Character::isDigit)) {
            player.sendMessage(
                    config.getMessage("passwordNeedsNumber")
            );
            return true;
        }

        if (config.requireSymbol()
                && password.chars().allMatch(Character::isLetterOrDigit)) {
            player.sendMessage(
                    config.getMessage("passwordNeedsSymbol")
            );
            return true;
        }

        for (String unsafe : config.getForbiddenPasswords()) {
            if (password.equalsIgnoreCase(unsafe)) {
                player.sendMessage(
                        config.getMessage("unsafePassword")
                );
                return true;
            }
        }

        authService.registerAsync(
                player.getUniqueId(),
                player.getName(),
                password,
                result -> plugin.getServer().getScheduler().runTask(
                        plugin,
                        () -> {
                            switch (result) {
case SUCCESS -> {
    player.removePotionEffect(PotionEffectType.BLINDNESS);

    player.sendMessage(
            config.getMessage("accountCreated")
    );
}

                                case ALREADY_REGISTERED ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "alreadyRegistered"
                                                )
                                        );

                                case USERNAME_TAKEN ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "usernameTaken"
                                                )
                                        );
                            }
                        }
                ),
                exception -> plugin.getServer().getScheduler().runTask(
                        plugin,
                        () -> {
                            player.sendMessage(
                                    config.getMessage("registerFailed")
                            );
                            exception.printStackTrace();
                        }
                )
        );

        return true;
    }
}
