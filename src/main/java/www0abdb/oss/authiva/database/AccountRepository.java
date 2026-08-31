package www0abdb.oss.authiva.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public final class AccountRepository {

    private final DatabaseManager databaseManager;

    public AccountRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public boolean exists(UUID uuid) throws SQLException {
        String sql = """
                SELECT 1
                FROM accounts
                WHERE uuid = ?
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public boolean existsByUsername(String username) throws SQLException {
        String sql = """
                SELECT 1
                FROM accounts
                WHERE username = ?
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    public void createAccount(
            UUID uuid,
            String username,
            String passwordHash
    ) throws SQLException {

        String sql = """
                INSERT INTO accounts (
                    uuid,
                    username,
                    password_hash,
                    created_at
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());
            statement.setString(2, username);
            statement.setString(3, passwordHash);
            statement.setLong(4, System.currentTimeMillis());

            statement.executeUpdate();
        }
    }


    public boolean updatePasswordHash(
            UUID uuid,
            String passwordHash
    ) throws SQLException {

        String sql = """
                UPDATE accounts
                SET password_hash = ?
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, passwordHash);
            statement.setString(2, uuid.toString());

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteAccount(UUID uuid) throws SQLException {

        String sql = """
                DELETE FROM accounts
                WHERE uuid = ?
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            return statement.executeUpdate() > 0;
        }
    }

    public Optional<AccountRecord> findByUsername(String username)
            throws SQLException {

        String sql = """
                SELECT uuid, username, password_hash, created_at
                FROM accounts
                WHERE username = ?
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(readAccount(resultSet));
            }
        }
    }

    public java.util.List<AccountRecord> findAll() throws SQLException {
        String sql = """
                SELECT uuid, username, password_hash, created_at
                FROM accounts
                ORDER BY username COLLATE NOCASE
                """;

        java.util.List<AccountRecord> accounts = new java.util.ArrayList<>();

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                accounts.add(readAccount(resultSet));
            }
        }

        return accounts;
    }

    public Optional<AccountRecord> findByUuid(UUID uuid)
            throws SQLException {

        String sql = """
                SELECT uuid, username, password_hash, created_at
                FROM accounts
                WHERE uuid = ?
                LIMIT 1
                """;

        try (PreparedStatement statement =
                     databaseManager.getConnection().prepareStatement(sql)) {

            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                return Optional.of(readAccount(resultSet));
            }
        }
    }

    private AccountRecord readAccount(ResultSet resultSet)
            throws SQLException {

        return new AccountRecord(
                UUID.fromString(resultSet.getString("uuid")),
                resultSet.getString("username"),
                resultSet.getString("password_hash"),
                resultSet.getLong("created_at")
        );
    }

    public record AccountRecord(
            UUID uuid,
            String username,
            String passwordHash,
            long createdAt
    ) {
    }
}
