package www0abdb.oss.authiva.auth;

import java.util.UUID;

public record AuthSession(
        UUID uuid,
        long createdAt,
        long expiresAt
) {

    public boolean isExpired(long currentTime) {
        return currentTime >= expiresAt;
    }
}
