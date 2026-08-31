package www0abdb.oss.authiva.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import www0abdb.oss.authiva.auth.AuthService;
import www0abdb.oss.authiva.config.AuthivaConfig;

public final class UnregisterCommand implements CommandExecutor {

    private final AuthService authService;
    private final JavaPlugin plugin;
    private final AuthivaConfig config;

    public UnregisterCommand(
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

        if (args.length != 1) {
            player.sendMessage(config.getMessage("unregisterUsage"));
            return true;
        }

        authService.unregisterAsync(
                player.getUniqueId(),
                args[0],
                result -> plugin.getServer().getScheduler().runTask(
                        plugin,
                        () -> {
                            switch (result) {
                                case SUCCESS ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "accountUnregistered"
                                                )
                                        );

                                case NOT_REGISTERED ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "notRegistered"
                                                )
                                        );

                                case INVALID_PASSWORD ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "invalidPassword"
                                                )
                                        );

                                case FAILED ->
                                        player.sendMessage(
                                                config.getMessage(
                                                        "unregisterFailed"
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
                                            "unregisterFailed"
                                    )
                            );
                            exception.printStackTrace();
                        }
                )
        );

        return true;
    }
}
