package www0abdb.oss.authiva.auth;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.bukkit.plugin.java.JavaPlugin;
import www0abdb.oss.authiva.config.AuthivaConfig;
import www0abdb.oss.authiva.database.AccountRepository;
import www0abdb.oss.authiva.database.DatabaseManager;
import www0abdb.oss.authiva.database.SessionRepository;

import static org.mockito.Mockito.*;

import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthServiceTest {

    @TempDir
    Path tempDirectory;

    private DatabaseManager databaseManager;
    private AuthService authService;

    @BeforeEach
    void setUp() throws SQLException {
        File databaseFile =
                tempDirectory.resolve("test.db").toFile();

        databaseManager = new DatabaseManager(databaseFile);
        databaseManager.initialize();

        AccountRepository repository =
                new AccountRepository(databaseManager);

        SessionRepository sessionRepository =
                new SessionRepository(databaseManager);

        AuthSessionManager sessions =
                new AuthSessionManager();

        AuthivaConfig config =
                mock(AuthivaConfig.class);

        when(config.isSessionEnabled())
                .thenReturn(true);

        when(config.getSessionDuration())
                .thenReturn(3L * 24L * 60L * 60L * 1000L);

        authService = new AuthService(
                repository,
                sessionRepository,
                sessions,
                config
        );
    }

    @AfterEach
    void tearDown() {
        if (databaseManager != null) {
            databaseManager.close();
        }
    }

    @Test
    void shouldRegisterAccount() throws SQLException {
        UUID uuid = UUID.randomUUID();

        AuthService.RegisterResult result =
                authService.register(
                        uuid,
                        "TestPlayer",
                        "StrongPassword123!"
                );

        assertEquals(
                AuthService.RegisterResult.SUCCESS,
                result
        );
    }

    @Test
    void shouldRejectDuplicateUuid() throws SQLException {
        UUID uuid = UUID.randomUUID();

        authService.register(
                uuid,
                "TestPlayer",
                "StrongPassword123!"
        );

        AuthService.RegisterResult result =
                authService.register(
                        uuid,
                        "AnotherPlayer",
                        "AnotherPassword123!"
                );

        assertEquals(
                AuthService.RegisterResult.ALREADY_REGISTERED,
                result
        );
    }
@Test
void shouldLoginWithCorrectPassword() throws SQLException {
    UUID uuid = UUID.randomUUID();

    authService.register(
            uuid,
            "TestPlayer",
            "StrongPassword123!"
    );

    authService.logout(uuid);

    AuthService.LoginResult result =
            authService.login(
                    uuid,
                    "StrongPassword123!"
            );

    assertEquals(
            AuthService.LoginResult.SUCCESS,
            result
    );

    assertTrue(authService.isAuthenticated(uuid));
}

@Test
void shouldRejectWrongPassword() throws SQLException {
    UUID uuid = UUID.randomUUID();

    authService.register(
            uuid,
            "TestPlayer",
            "StrongPassword123!"
    );

    authService.logout(uuid);

    AuthService.LoginResult result =
            authService.login(
                    uuid,
                    "WrongPassword123!"
            );

    assertEquals(
            AuthService.LoginResult.INVALID_PASSWORD,
            result
    );

    assertFalse(authService.isAuthenticated(uuid));
}

    @Test
    void shouldRejectUnregisteredPlayer() throws SQLException {
        UUID uuid = UUID.randomUUID();

        AuthService.LoginResult result =
                authService.login(
                        uuid,
                        "SomePassword123!"
                );

        assertEquals(
                AuthService.LoginResult.NOT_REGISTERED,
                result
        );
    }

    @Test
    void shouldLogoutPlayer() throws SQLException {
        UUID uuid = UUID.randomUUID();

        authService.register(
                uuid,
                "TestPlayer",
                "StrongPassword123!"
        );

        authService.login(
                uuid,
                "StrongPassword123!"
        );

        assertTrue(authService.isAuthenticated(uuid));

        authService.logout(uuid);

        assertFalse(authService.isAuthenticated(uuid));
    }
}
