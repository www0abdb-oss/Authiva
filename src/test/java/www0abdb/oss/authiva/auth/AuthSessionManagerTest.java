package www0abdb.oss.authiva.auth;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthSessionManagerTest {

    @Test
    void playerShouldNotBeAuthenticatedInitially() {
        AuthSessionManager sessions = new AuthSessionManager();
        UUID uuid = UUID.randomUUID();

        assertFalse(sessions.isAuthenticated(uuid));
    }

    @Test
    void authenticateShouldMarkPlayerAsAuthenticated() {
        AuthSessionManager sessions = new AuthSessionManager();
        UUID uuid = UUID.randomUUID();

        sessions.authenticate(uuid);

        assertTrue(sessions.isAuthenticated(uuid));
    }

    @Test
    void unauthenticateShouldRemovePlayer() {
        AuthSessionManager sessions = new AuthSessionManager();
        UUID uuid = UUID.randomUUID();

        sessions.authenticate(uuid);
        sessions.unauthenticate(uuid);

        assertFalse(sessions.isAuthenticated(uuid));
    }

    @Test
    void clearShouldRemoveAllSessions() {
        AuthSessionManager sessions = new AuthSessionManager();

        UUID first = UUID.randomUUID();
        UUID second = UUID.randomUUID();

        sessions.authenticate(first);
        sessions.authenticate(second);
        sessions.clear();

        assertFalse(sessions.isAuthenticated(first));
        assertFalse(sessions.isAuthenticated(second));
    }
}
