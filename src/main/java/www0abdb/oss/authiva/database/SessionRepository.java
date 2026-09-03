package www0abdb.oss.authiva.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class SessionRepository {

    private final DatabaseManager databaseManager;

    public SessionRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void save(UUID uuid, String tokenHash, long createdAt, long expiresAt)
            throws SQLException {

        String sql = """
                INSERT INTO sessions (
                    uuid,
                    token_hash,
                    created_at,
                    expires_at
                )
                VALUES (?, ?, ?, ?)
                ON CONFLICT(uuid) DO UPDATE SET
                    token_hash = excluded.token_hash,
                    created_at = excluded.created_at,
                    expires_at = excluded.expires_at
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, tokenHash);
            statement.setLong(3, createdAt);
            statement.setLong(4, expiresAt);

            statement.executeUpdate();
        }
    }

    public Optional<SessionRecord> findByUuid(UUID uuid)
            throws SQLException {

        String sql = """
                SELECT uuid, token_hash, created_at, expires_at
                FROM sessions
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(new SessionRecord(
                        UUID.fromString(resultSet.getString("uuid")),
                        resultSet.getString("token_hash"),
                        resultSet.getLong("created_at"),
                        resultSet.getLong("expires_at")
                ));
            }
        }
    }

    public boolean delete(UUID uuid) throws SQLException {
        String sql = """
                DELETE FROM sessions
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            return statement.executeUpdate() > 0;
        }
    }

    public int deleteExpired(long currentTime) throws SQLException {
        String sql = """
                DELETE FROM sessions
                WHERE expires_at <= ?
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setLong(1, currentTime);

            return statement.executeUpdate();
        }
    }

    public record SessionRecord(
            UUID uuid,
            String tokenHash,
            long createdAt,
            long expiresAt
    ) {
    }
}
