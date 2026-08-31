package www0abdb.oss.authiva;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import www0abdb.oss.authiva.auth.AuthListener;
import www0abdb.oss.authiva.auth.AuthService;
import www0abdb.oss.authiva.auth.AuthSessionManager;
import www0abdb.oss.authiva.commands.LoginCommand;
import www0abdb.oss.authiva.commands.UnregisterCommand;
import www0abdb.oss.authiva.commands.ChangePasswordCommand;
import www0abdb.oss.authiva.commands.LogoutCommand;
import www0abdb.oss.authiva.commands.RegisterCommand;
import www0abdb.oss.authiva.database.AccountRepository;
import www0abdb.oss.authiva.database.DatabaseManager;

import java.io.File;
import java.sql.SQLException;

import www0abdb.oss.authiva.config.AuthivaConfig;

public final class Authiva extends JavaPlugin {

    private DatabaseManager databaseManager;
    private AuthService authService;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        File databaseFile = new File(getDataFolder(), "authiva.db");
        databaseManager = new DatabaseManager(databaseFile);

        try {
            databaseManager.initialize();

            AccountRepository accountRepository =
                    new AccountRepository(databaseManager);

            AuthSessionManager sessionManager =
                    new AuthSessionManager();

            authService = new AuthService(
                    accountRepository,
                    sessionManager
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

        AuthivaConfig authivaConfig = new AuthivaConfig(this);

        getServer().getPluginManager().registerEvents(
                new AuthListener(authService, authivaConfig),
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

        new Metrics(this, 33742);

        getLogger().info("Authiva enabled.");
    }

    @Override
    public void onDisable() {
        if (authService != null) {
            authService.shutdown();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("Authiva disabled.");
    }
}
