package www0abdb.oss.authiva.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import www0abdb.oss.authiva.auth.AuthService;
import www0abdb.oss.authiva.config.AuthivaConfig;
import www0abdb.oss.authiva.database.AccountRepository;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public final class AdminAuthivaCommand implements CommandExecutor {

    private final AuthService authService;
    private final JavaPlugin plugin;
    private final AuthivaConfig config;

    public AdminAuthivaCommand(
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
        if (!sender.isOp()) {
            sender.sendMessage(config.getMessage("adminOnly"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase(Locale.ROOT)) {
            case "setpassword" -> handleSetPassword(sender, args);
            case "unregister", "delete" -> handleUnregister(sender, args);
            case "logout" -> handleLogout(sender, args);
            case "info" -> handleInfo(sender, args);
            case "list" -> handleList(sender);
            case "reload" -> handleReload(sender);
            case "help" -> sendHelp(sender);
            default -> sendHelp(sender);
        }

        return true;
    }

    private void handleSetPassword(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(config.getMessage("adminSetPasswordUsage"));
            return;
        }

        String username = args[1];
        String newPassword = args[2];

        String validationError = validatePassword(newPassword);

        if (validationError != null) {
            sender.sendMessage(validationError);
            return;
        }

        authService.adminSetPasswordAsync(
                username,
                newPassword,
                result -> runSync(() -> {
                    String key = switch (result) {
                        case SUCCESS -> "adminPasswordChanged";
                        case NOT_REGISTERED -> "adminPlayerNotRegistered";
                        case FAILED -> "adminSetPasswordFailed";
                    };

                    sender.sendMessage(
                            config.getMessage(
                                    key,
                                    "{player}",
                                    username
                            )
                    );
                }),
                exception -> runSync(() -> {
                    sender.sendMessage(
                            config.getMessage("adminSetPasswordFailed")
                    );

                    plugin.getLogger().warning(
                            "Admin password change failed for "
                                    + username
                                    + ": "
                                    + exception.getMessage()
                    );
                })
        );
    }

    private void handleUnregister(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(config.getMessage("adminUnregisterUsage"));
            return;
        }

        String username = args[1];

        authService.adminUnregisterAsync(
                username,
                result -> runSync(() -> {
                    String key = switch (result) {
                        case SUCCESS -> "adminAccountUnregistered";
                        case NOT_REGISTERED -> "adminPlayerNotRegistered";
                        case FAILED -> "adminUnregisterFailed";
                    };

                    sender.sendMessage(
                            config.getMessage(
                                    key,
                                    "{player}",
                                    username
                            )
                    );
                }),
                exception -> runSync(() -> {
                    sender.sendMessage(
                            config.getMessage("adminUnregisterFailed")
                    );

                    plugin.getLogger().warning(
                            "Admin account deletion failed for "
                                    + username
                                    + ": "
                                    + exception.getMessage()
                    );
                })
        );
    }

    private void handleLogout(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(config.getMessage("adminLogoutUsage"));
            return;
        }

        String username = args[1];

        authService.adminLogoutAsync(
                username,
                result -> runSync(() -> {
                    switch (result) {
                        case SUCCESS -> sender.sendMessage(
                                config.getMessage(
                                        "adminPlayerLoggedOut",
                                        "{player}",
                                        username
                                )
                        );

                        case NOT_REGISTERED -> sender.sendMessage(
                                config.getMessage(
                                        "adminPlayerNotRegistered"
                                )
                        );
                    }
                }),
                exception -> runSync(() -> {
                    sender.sendMessage(
                            config.getMessage("adminLogoutFailed")
                    );

                    plugin.getLogger().warning(
                            "Admin logout failed for "
                                    + username
                                    + ": "
                                    + exception.getMessage()
                    );
                })
        );
    }

    private void handleInfo(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(config.getMessage("adminInfoUsage"));
            return;
        }

        String username = args[1];

        plugin.getServer().getScheduler().runTaskAsynchronously(
                plugin,
                () -> {
                    try {
                        var account = authService.adminFindAccount(username);

                        plugin.getServer().getScheduler().runTask(
                                plugin,
                                () -> {
                                    if (account.isEmpty()) {
                                        sender.sendMessage(
                                                config.getMessage(
                                                        "adminPlayerNotRegistered"
                                                )
                                        );
                                        return;
                                    }

                                    AccountRepository.AccountRecord record =
                                            account.get();

                                    String createdAt =
                                            new SimpleDateFormat(
                                                    "yyyy-MM-dd HH:mm:ss"
                                            ).format(
                                                    new Date(record.createdAt())
                                            );

                                    sender.sendMessage(
                                            config.getMessage(
                                                    "adminInfo",
                                                    "{player}",
                                                    record.username(),
                                                    "{uuid}",
                                                    record.uuid().toString(),
                                                    "{created}",
                                                    createdAt
                                            )
                                    );
                                }
                        );
                    } catch (Exception exception) {
                        plugin.getServer().getScheduler().runTask(
                                plugin,
                                () -> sender.sendMessage(
                                        config.getMessage(
                                                "adminInfoFailed"
                                        )
                                )
                        );

                        plugin.getLogger().warning(
                                "Admin account info failed for "
                                        + username
                                        + ": "
                                        + exception.getMessage()
                        );
                    }
                }
        );
    }

    private void handleList(CommandSender sender) {
        plugin.getServer().getScheduler().runTaskAsynchronously(
                plugin,
                () -> {
                    try {
                        List<AccountRepository.AccountRecord> accounts =
                                authService.adminListAccounts();

                        plugin.getServer().getScheduler().runTask(
                                plugin,
                                () -> {
                                    if (accounts.isEmpty()) {
                                        sender.sendMessage(
                                                config.getMessage(
                                                        "adminListEmpty"
                                                )
                                        );
                                        return;
                                    }

                                    sender.sendMessage(
                                            config.getMessage(
                                                    "adminListHeader"
                                            )
                                    );

                                    for (
                                            AccountRepository.AccountRecord account
                                            : accounts
                                    ) {
                                        sender.sendMessage(
                                                config.getMessage(
                                                        "adminListEntry",
                                                        "{player}",
                                                        account.username()
                                                )
                                        );
                                    }

                                    sender.sendMessage(
                                            config.getMessage(
                                                    "adminListFooter",
                                                    "{count}",
                                                    String.valueOf(
                                                            accounts.size()
                                                    )
                                            )
                                    );
                                }
                        );
                    } catch (Exception exception) {
                        plugin.getServer().getScheduler().runTask(
                                plugin,
                                () -> sender.sendMessage(
                                        config.getMessage(
                                                "adminListFailed"
                                        )
                                )
                        );

                        plugin.getLogger().warning(
                                "Admin account list failed: "
                                        + exception.getMessage()
                        );
                    }
                }
        );
    }

    private void handleReload(CommandSender sender) {
        config.reload();

        sender.sendMessage(
                config.getMessage("adminReloaded")
        );
    }

    private String validatePassword(String password) {
        int min = config.getMinPasswordLength();
        int max = config.getMaxPasswordLength();

        if (password.length() < min) {
            return config.getMessage(
                    "passwordTooShort",
                    "{min}",
                    String.valueOf(min)
            );
        }

        if (password.length() > max) {
            return config.getMessage(
                    "passwordTooLong",
                    "{max}",
                    String.valueOf(max)
            );
        }

        if (!config.allowLetters()
                && password.chars().anyMatch(Character::isLetter)) {
            return config.getMessage("invalidPasswordCharacters");
        }

        if (!config.allowNumbers()
                && password.chars().anyMatch(Character::isDigit)) {
            return config.getMessage("invalidPasswordCharacters");
        }

        if (!config.allowSymbols()
                && password.chars().anyMatch(
                c -> !Character.isLetterOrDigit(c))) {
            return config.getMessage("invalidPasswordCharacters");
        }

        if (config.requireLetter()
                && password.chars().noneMatch(Character::isLetter)) {
            return config.getMessage("passwordNeedsLetter");
        }

        if (config.requireNumber()
                && password.chars().noneMatch(Character::isDigit)) {
            return config.getMessage("passwordNeedsNumber");
        }

        if (config.requireSymbol()
                && password.chars().allMatch(
                Character::isLetterOrDigit)) {
            return config.getMessage("passwordNeedsSymbol");
        }

        for (String unsafe : config.getForbiddenPasswords()) {
            if (password.equalsIgnoreCase(unsafe)) {
                return config.getMessage("unsafePassword");
            }
        }

        return null;
    }

    private void runSync(Runnable task) {
        plugin.getServer().getScheduler().runTask(plugin, task);
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(config.getMessage("adminHelp"));
    }
}
