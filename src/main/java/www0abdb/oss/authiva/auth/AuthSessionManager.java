package www0abdb.oss.authiva.auth;

import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AuthSessionManager {

    private final Set<UUID> authenticatedPlayers =
            ConcurrentHashMap.newKeySet();

    private final Map<UUID, Integer> loginAttempts =
            new ConcurrentHashMap<>();

    private final Map<UUID, BukkitTask> timeoutTasks =
            new ConcurrentHashMap<>();

    public void authenticate(UUID uuid) {
        authenticatedPlayers.add(uuid);
        loginAttempts.remove(uuid);
        cancelTimeout(uuid);
    }

    public void unauthenticate(UUID uuid) {
        authenticatedPlayers.remove(uuid);
        cancelTimeout(uuid);
    }

    public boolean isAuthenticated(UUID uuid) {
        return authenticatedPlayers.contains(uuid);
    }

    public int incrementLoginAttempts(UUID uuid) {
        return loginAttempts.merge(uuid, 1, Integer::sum);
    }

    public int getLoginAttempts(UUID uuid) {
        return loginAttempts.getOrDefault(uuid, 0);
    }

    public void resetLoginAttempts(UUID uuid) {
        loginAttempts.remove(uuid);
    }

    public void setTimeoutTask(UUID uuid, BukkitTask task) {
        cancelTimeout(uuid);
        timeoutTasks.put(uuid, task);
    }

    public void cancelTimeout(UUID uuid) {
        BukkitTask task = timeoutTasks.remove(uuid);

        if (task != null) {
            task.cancel();
        }
    }

    public void remove(UUID uuid) {
        authenticatedPlayers.remove(uuid);
        loginAttempts.remove(uuid);
        cancelTimeout(uuid);
    }

    public void clear() {
        authenticatedPlayers.clear();
        loginAttempts.clear();

        for (BukkitTask task : timeoutTasks.values()) {
            task.cancel();
        }

        timeoutTasks.clear();
    }
}
