package www0abdb.oss.authiva.auth;

import www0abdb.oss.authiva.database.AccountRepository;
import www0abdb.oss.authiva.security.PasswordHasher;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class AuthService {

    private final AccountRepository accountRepository;
    private final AuthSessionManager sessionManager;
    private final ExecutorService executor =
            Executors.newSingleThreadExecutor(r -> {
                Thread thread = new Thread(r, "Authiva-Worker");
                thread.setDaemon(true);
                return thread;
            });

    public AuthService(
            AccountRepository accountRepository,
            AuthSessionManager sessionManager
    ) {
        this.accountRepository = accountRepository;
        this.sessionManager = sessionManager;
    }

    public void shutdown() {
        executor.shutdownNow();
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public RegisterResult register(
            UUID uuid,
            String username,
            String password
    ) throws SQLException {

        if (accountRepository.exists(uuid)) {
            return RegisterResult.ALREADY_REGISTERED;
        }

        if (accountRepository.existsByUsername(username)) {
            return RegisterResult.USERNAME_TAKEN;
        }

        String passwordHash = PasswordHasher.hash(password);

        accountRepository.createAccount(
                uuid,
                username,
                passwordHash
        );

        return RegisterResult.SUCCESS;
    }

    public LoginResult login(
            UUID uuid,
            String password
    ) throws SQLException {

        if (sessionManager.isAuthenticated(uuid)) {
            return LoginResult.ALREADY_LOGGED_IN;
        }

        var account = accountRepository.findByUuid(uuid);

        if (account.isEmpty()) {
            return LoginResult.NOT_REGISTERED;
        }

        if (!PasswordHasher.verify(
                password,
                account.get().passwordHash()
        )) {
            return LoginResult.INVALID_PASSWORD;
        }


        sessionManager.authenticate(uuid);
        return LoginResult.SUCCESS;
    }

    public void registerAsync(
            UUID uuid,
            String username,
            String password,
            java.util.function.Consumer<RegisterResult> callback,
            java.util.function.Consumer<Exception> errorCallback
    ) {
        executor.execute(() -> {
            try {
                RegisterResult result = register(uuid, username, password);
                callback.accept(result);
            } catch (Exception exception) {
                errorCallback.accept(exception);
            }
        });
    }

    public void loginAsync(
            UUID uuid,
            String password,
            java.util.function.Consumer<LoginResult> callback,
            java.util.function.Consumer<Exception> errorCallback
    ) {
        executor.execute(() -> {
            try {
                LoginResult result = login(uuid, password);
                callback.accept(result);
            } catch (Exception exception) {
                errorCallback.accept(exception);
            }
        });
    }

    public boolean hasAccount(UUID uuid) throws SQLException {
        return accountRepository.exists(uuid);
    }

    public void logout(UUID uuid) {
        sessionManager.unauthenticate(uuid);
    }

    public boolean isAuthenticated(UUID uuid) {
        return sessionManager.isAuthenticated(uuid);
    }

    public enum RegisterResult {
        SUCCESS,
        ALREADY_REGISTERED,
        USERNAME_TAKEN
    }

    public enum LoginResult {
        SUCCESS,
        ALREADY_LOGGED_IN,
        NOT_REGISTERED,
        INVALID_PASSWORD
    }
}
