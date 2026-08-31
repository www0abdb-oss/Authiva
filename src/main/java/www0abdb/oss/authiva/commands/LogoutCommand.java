package www0abdb.oss.authiva.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import www0abdb.oss.authiva.auth.AuthService;

public final class LogoutCommand implements CommandExecutor {

    private final AuthService authService;

    public LogoutCommand(AuthService authService) {
        this.authService = authService;
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

        if (args.length != 0) {
            player.sendMessage(
                    ChatColor.YELLOW
                            + "Usage: /logout"
            );
            return true;
        }

        if (!authService.isAuthenticated(player.getUniqueId())) {
            player.sendMessage(
                    ChatColor.YELLOW
                            + "You are not logged in."
            );
            return true;
        }

        authService.logout(player.getUniqueId());

        player.sendMessage(
                ChatColor.GREEN
                        + "You have been logged out."
        );

        return true;
    }
}
