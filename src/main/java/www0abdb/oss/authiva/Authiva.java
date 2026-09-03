package www0abdb.oss.authiva;

import org.bukkit.plugin.java.JavaPlugin;

import dev.faststats.bukkit.BukkitContext;
import dev.faststats.Metrics;

import www0abdb.oss.authiva.auth.AuthListener;
import www0abdb.oss.authiva.auth.AuthService;
import www0abdb.oss.authiva.auth.AuthSessionManager;
import www0abdb.oss.authiva.commands.AdminAuthivaCommand;
import www0abdb.oss.authiva.commands.LoginCommand;
import www0abdb.oss.authiva.commands.UnregisterCommand;
import www0abdb.oss.authiva.commands.ChangePasswordCommand;
import www0abdb.oss.authiva.commands.LogoutCommand;
import www0abdb.oss.authiva.commands.RegisterCommand;
import www0abdb.oss.authiva.database.AccountRepository;
import www0abdb.oss.authiva.database.SessionRepository;
import www0abdb.oss.authiva.database.DatabaseManager;
import www0abdb.oss.authiva.update.UpdateChecker;
import www0abdb.oss.authiva.config.AuthivaConfig;

import java.io.File;
import java.sql.SQLException;

public final class Authiva extends JavaPlugin {

    private final BukkitContext context =
            new BukkitContext.Factory(
                    this,
                    "b1cbccbceea067a574b8175a02c0b833"
            )
            .metrics(Metrics.Factory::create)
            .create();

    private DatabaseManager databaseManager;
    private AuthService authService;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        File databaseFile = new File(getDataFolder(), "authiva.db");
        databaseManager = new DatabaseManager(databaseFile);

        AuthivaConfig authivaConfig = new AuthivaConfig(this);

        try {
            databaseManager.initialize();

            AccountRepository accountRepository =
                    new AccountRepository(databaseManager);

            SessionRepository sessionRepository =
                    new SessionRepository(databaseManager);

            AuthSessionManager sessionManager =
                    new AuthSessionManager();

            authService = new AuthService(
                    accountRepository,
                    sessionRepository,
                    sessionManager,
                    authivaConfig
            );

            sessionRepository.deleteExpired(
                    System.currentTimeMillis()
            );

        } catch (SQLException exception) {
            getLogger().severe("Failed to initialize Authiva database.");
            exception.printStackTrace();

            if (databaseManager != null) {
                databaseManager.close();
            }

            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        getServer().getPluginManager().registerEvents(
                new AuthListener(
                        authService,
                        authivaConfig,
                        this
                ),
                this
        );

        getCommand("register").setExecutor(
                new RegisterCommand(
                        authService,
                        this,
                        authivaConfig
                )
        );

        getCommand("login").setExecutor(
                new LoginCommand(
                        authService,
                        this,
                        authivaConfig
                )
        );

        getCommand("logout").setExecutor(
                new LogoutCommand(authService)
        );

        getCommand("authiva").setExecutor(
                new AdminAuthivaCommand(
                        authService,
                        this,
                        authivaConfig
                )
        );

        getCommand("changepassword").setExecutor(
                new ChangePasswordCommand(
                        authService,
                        this,
                        authivaConfig
                )
        );

        getCommand("unregister").setExecutor(
                new UnregisterCommand(
                        authService,
                        this,
                        authivaConfig
                )
        );

        // bStats
        new org.bstats.bukkit.Metrics(this, 33742);

        if (authivaConfig.checkForUpdates()) {
            updateChecker = new UpdateChecker(this);
            updateChecker.start();
        }

        // FastStats: Authiva is now fully initialized
        context.ready();

        getLogger().info("Authiva is running on Paper");
        getLogger().info("Authiva has been enabled successfully!");
        getLogger().info("GitHub: https://github.com/www0abdb-oss/Authiva");
    }

    @Override
    public void onDisable() {
        if (updateChecker != null) {
            updateChecker.stop();
        }

        if (authService != null) {
            authService.shutdown();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        // FastStats
        context.shutdown();

        getLogger().info("Authiva disabled.");
    }
}
