package www0abdb.oss.authiva.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import www0abdb.oss.authiva.auth.AuthService;
import www0abdb.oss.authiva.config.AuthivaConfig;

import java.sql.SQLException;

public final class LoginCommand implements CommandExecutor {

    private final AuthService authService;
    private final JavaPlugin plugin;
    private final AuthivaConfig config;

    public LoginCommand(
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
                    ChatColor.RED
                            + "Only players can use this command."
            );
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(
                    config.getMessage("loginUsage")
            );
            return true;
        }

        authService.loginAsync(
                player.getUniqueId(),
                args[0],
                result -> plugin.getServer().getScheduler().runTask(
                        plugin,
                        () -> {
                            switch (result) {
                                case SUCCESS -> player.sendMessage(
                                        ChatColor.GREEN
                                                + "Login successful."
                                );

                                case ALREADY_LOGGED_IN -> player.sendMessage(
                                        ChatColor.YELLOW
                                                + "You are already logged in."
                                );

                                case NOT_REGISTERED -> player.sendMessage(
                                        ChatColor.RED
                                                + "You do not have an account. "
                                                + "Use /register <password>."
                                );

                                case INVALID_PASSWORD -> player.sendMessage(
                                        ChatColor.RED
                                                + "Invalid password."
                                );
                            }
                        }
                ),
                exception -> plugin.getServer().getScheduler().runTask(
                        plugin,
                        () -> {
                            player.sendMessage(
                                    ChatColor.RED
                                            + "Authentication service is unavailable."
                            );
                            getLoggerSafe(exception);
                        }
                )
        );

        return true;
    }

    private void getLoggerSafe(Exception exception) {
        exception.printStackTrace();
    }
}
