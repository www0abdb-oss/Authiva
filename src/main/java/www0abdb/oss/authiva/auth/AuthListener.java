package www0abdb.oss.authiva.auth;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import www0abdb.oss.authiva.config.AuthivaConfig;

public final class AuthListener implements Listener {

    private final AuthService authService;
    private final AuthivaConfig config;
    private final JavaPlugin plugin;

    public AuthListener(
            AuthService authService,
            AuthivaConfig config,
            JavaPlugin plugin
    ) {
        this.authService = authService;
        this.config = config;
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (config.isBypassEnabled()
                && player.hasPermission(
                config.getBypassPermission())) {

            authService.authenticate(uuid);
            return;
        }

        try {

            boolean registered =
                    authService.hasAccount(uuid);

            startAuthenticationTimer(player);

            if (config.sendReminderOnJoin()) {

                if (registered) {
                    send(
                            player,
                            "login-prompt",
                            "{player}",
                            player.getName()
                    );
                } else {
                    send(
                            player,
                            "register-prompt",
                            "{player}",
                            player.getName()
                    );
                }
            }

        } catch (SQLException exception) {

            send(player, "auth-unavailable");

            plugin.getLogger().warning(
                    "Failed to check authentication account for "
                            + player.getName()
            );

            exception.printStackTrace();
        }
    }

    private void startAuthenticationTimer(Player player) {

        UUID uuid = player.getUniqueId();

        int timeout =
                Math.max(1, config.getTimeout());

        int interval =
                Math.max(1, config.getReminderInterval());

        BukkitTask task =
                plugin.getServer()
                        .getScheduler()
                        .runTaskTimer(
                                plugin,
                                new Runnable() {

                                    int elapsed = 0;

                                    @Override
                                    public void run() {

                        if (!player.isOnline()) {
                            authService.getSessionManager().remove(uuid);
                            return;
                        }

                                        if (authService
                                                .isAuthenticated(uuid)) {
                                            return;
                                        }

                                        elapsed += interval;

                                        if (elapsed >= timeout) {

                                            send(
                                                    player,
                                                    "login-timeout"
                                            );

                                            authService.logout(uuid);

                                            plugin.getServer()
                                                    .getScheduler()
                                                    .runTask(
                                                            plugin,
                                                            () -> {
                                                                if (player.isOnline()) {
                                                                    player.kickPlayer(
                                                                            config.getMessage(
                                                                                    "login-timeout"
                                                                            )
                                                                    );
                                                                }
                                                            }
                                                    );

                                            return;
                                        }

                                        if (config.remindersEnabled()) {

                                            int remaining =
                                                    Math.max(
                                                            0,
                                                            timeout - elapsed
                                                    );

                                            send(
                                                    player,
                                                    "reminder-with-time",
                                                    "{seconds}",
                                                    String.valueOf(
                                                            remaining
                                                    )
                                            );
                                        }
                                    }
                                },
                                interval * 20L,
                                interval * 20L
                        );

        authService.getSessionManager()
                .setTimeoutTask(uuid, task);
    }

    private void send(
            Player player,
            String key,
            String... replacements
    ) {
        List<String> messages =
                config.getMessages(
                        key,
                        replacements
                );

        for (String message : messages) {
            player.sendMessage(message);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerMove(PlayerMoveEvent event) {

        if (!config.freezeMovement()) {
            return;
        }

        if (event.getTo() == null) {
            return;
        }

        if (event.getFrom().getX() == event.getTo().getX()
                && event.getFrom().getY() == event.getTo().getY()
                && event.getFrom().getZ() == event.getTo().getZ()) {
            return;
        }

        Player player = event.getPlayer();

        if (!authService.isAuthenticated(
                player.getUniqueId())) {

            event.setTo(event.getFrom());
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onEntityTarget(EntityTargetEvent event) {

        if (!config.preventMobTargeting()) {
            return;
        }

        if (!(event.getTarget() instanceof Player player)) {
            return;
        }

        if (!authService.isAuthenticated(
                player.getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!config.blockInteraction()) {
            return;
        }

        Player player = event.getPlayer();

        if (!authService.isAuthenticated(
                player.getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onEntityDamageByEntity(
            EntityDamageByEntityEvent event
    ) {

        if (!config.blockAttacks()) {
            return;
        }

        if (!(event.getDamager() instanceof Player player)) {
            return;
        }

        if (!authService.isAuthenticated(
                player.getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onEntityDamage(EntityDamageEvent event) {

        if (!config.blockDamage()) {
            return;
        }

        if (!(event.getEntity() instanceof Player player)) {
            return;
        }

        if (!authService.isAuthenticated(
                player.getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerDropItem(
            PlayerDropItemEvent event
    ) {

        if (!config.blockItemDrop()) {
            return;
        }

        if (!authService.isAuthenticated(
                event.getPlayer().getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerPickupItem(
            PlayerPickupItemEvent event
    ) {

        if (!config.blockItemPickup()) {
            return;
        }

        if (!authService.isAuthenticated(
                event.getPlayer().getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onInventoryClick(
            InventoryClickEvent event
    ) {

        if (!config.blockInventory()) {
            return;
        }

        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        if (!authService.isAuthenticated(
                player.getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onHeldItemChange(
            PlayerItemHeldEvent event
    ) {

        if (!config.blockHeldItemChange()) {
            return;
        }

        if (!authService.isAuthenticated(
                event.getPlayer().getUniqueId())) {

            event.setCancelled(true);
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST,
            ignoreCancelled = true
    )
    public void onPlayerCommand(
            PlayerCommandPreprocessEvent event
    ) {

        Player player = event.getPlayer();

        if (authService.isAuthenticated(
                player.getUniqueId())) {
            return;
        }

        String message =
                event.getMessage().trim();

        if (config.allowHelp()
                && message.equalsIgnoreCase("/help")) {
            return;
        }

        if (config.isCommandAllowed(message)) {
            return;
        }

        event.setCancelled(true);

        send(
                player,
                "login-required"
        );
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        authService.logout(
                event.getPlayer().getUniqueId()
        );
    }
}
