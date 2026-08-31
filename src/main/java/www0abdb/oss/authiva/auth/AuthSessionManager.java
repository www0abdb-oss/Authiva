package www0abdb.oss.authiva.auth;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AuthSessionManager {

    private final Set<UUID> authenticatedPlayers =
            ConcurrentHashMap.newKeySet();

    public void authenticate(UUID uuid) {
        authenticatedPlayers.add(uuid);
    }

    public void unauthenticate(UUID uuid) {
        authenticatedPlayers.remove(uuid);
    }

    public boolean isAuthenticated(UUID uuid) {
        return authenticatedPlayers.contains(uuid);
    }

    public void clear() {
        authenticatedPlayers.clear();
    }
}
