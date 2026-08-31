package www0abdb.oss.authiva.auth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;

import www0abdb.oss.authiva.config.AuthivaConfig;

public final class AuthListener implements Listener {

    private final AuthService authService;
    private final AuthivaConfig config;

    public AuthListener(
            AuthService authService,
            AuthivaConfig config
    ) {
        this.authService = authService;
        this.config = config;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        try {
            if (authService.hasAccount(player.getUniqueId())) {
                player.sendMessage(
                        config.getMessage("loginPrompt")
                );
            } else {
                player.sendMessage(
                        config.getMessage("noAccount")
                );

                player.sendMessage(
                        config.getMessage("registerPrompt")
                );
            }
        } catch (SQLException exception) {
            player.sendMessage(
                    ChatColor.RED
                            + "Authentication system is temporarily unavailable."
            );
        }
    }

    /*
     * Block only actual position changes.
     * Camera rotation remains free.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) {
            return;
        }

        if (event.getFrom().getX() == event.getTo().getX()
                && event.getFrom().getY() == event.getTo().getY()
                && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        Player player = event.getPlayer();

        if (!authService.isAuthenticated(player.getUniqueId())) {
            event.setTo(event.getFrom());
        }
    }

    /*
     * Prevent mobs from targeting unauthenticated players.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player player)) {
            return;
        }

        if (!authService.isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    /*
     * Unauthenticated players cannot interact with blocks/items.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!authService.isAuthenticated(
                event.getPlayer().getUniqueId()
        )) {
            event.setCancelled(true);
        }
    }

    /*
     * Prevent unauthenticated players from attacking entities.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!authService.isAuthenticated(player.getUniqueId())) {
            event.setCancelled(true);
        }
    }

    /*
     * Only login and register are available before authentication.
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();

        if (authService.isAuthenticated(player.getUniqueId())) {
            return;
        }

        String message = event.getMessage().trim();

        if (config.isCommandAllowed(message)) {
            return;
        }

        event.setCancelled(true);

        player.sendMessage(
                config.getMessage("loginRequired")
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        authService.logout(event.getPlayer().getUniqueId());
    }
}
