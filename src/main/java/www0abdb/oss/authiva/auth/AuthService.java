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


    public ChangePasswordResult changePassword(
            UUID uuid,
            String currentPassword,
            String newPassword
    ) throws SQLException {

        var account = accountRepository.findByUuid(uuid);

        if (account.isEmpty()) {
            return ChangePasswordResult.NOT_REGISTERED;
        }

        if (!PasswordHasher.verify(
                currentPassword,
                account.get().passwordHash()
        )) {
            return ChangePasswordResult.INVALID_CURRENT_PASSWORD;
        }

        String newPasswordHash = PasswordHasher.hash(newPassword);

        if (!accountRepository.updatePasswordHash(
                uuid,
                newPasswordHash
        )) {
            return ChangePasswordResult.FAILED;
        }

        return ChangePasswordResult.SUCCESS;
    }

    public void changePasswordAsync(
            UUID uuid,
            String currentPassword,
            String newPassword,
            java.util.function.Consumer<ChangePasswordResult> callback,
            java.util.function.Consumer<Exception> errorCallback
    ) {
        executor.execute(() -> {
            try {
                ChangePasswordResult result = changePassword(
                        uuid,
                        currentPassword,
                        newPassword
                );
                callback.accept(result);
            } catch (Exception exception) {
                errorCallback.accept(exception);
            }
        });
    }

    public UnregisterResult unregister(
            UUID uuid,
            String password
    ) throws SQLException {

        var account = accountRepository.findByUuid(uuid);

        if (account.isEmpty()) {
            return UnregisterResult.NOT_REGISTERED;
        }

        if (!PasswordHasher.verify(
                password,
                account.get().passwordHash()
        )) {
            return UnregisterResult.INVALID_PASSWORD;
        }

        if (!accountRepository.deleteAccount(uuid)) {
            return UnregisterResult.FAILED;
        }

        sessionManager.unauthenticate(uuid);

        return UnregisterResult.SUCCESS;
    }

    public void unregisterAsync(
            UUID uuid,
            String password,
            java.util.function.Consumer<UnregisterResult> callback,
            java.util.function.Consumer<Exception> errorCallback
    ) {
        executor.execute(() -> {
            try {
                UnregisterResult result = unregister(uuid, password);
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


    public enum ChangePasswordResult {
        SUCCESS,
        NOT_REGISTERED,
        INVALID_CURRENT_PASSWORD,
        FAILED
    }

    public enum UnregisterResult {
        SUCCESS,
        NOT_REGISTERED,
        INVALID_PASSWORD,
        FAILED
    }

    public enum LoginResult {
        SUCCESS,
        ALREADY_LOGGED_IN,
        NOT_REGISTERED,
        INVALID_PASSWORD
    }
}
