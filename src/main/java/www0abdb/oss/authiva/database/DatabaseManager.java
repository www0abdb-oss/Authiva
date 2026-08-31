package www0abdb.oss.authiva.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class DatabaseManager implements AutoCloseable {

    private final File databaseFile;
    private Connection connection;

    public DatabaseManager(File databaseFile) {
        this.databaseFile = databaseFile;
    }

    public void initialize() throws SQLException {
        File parent = databaseFile.getParentFile();

        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new SQLException(
                    "Could not create database directory: " + parent
            );
        }

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + databaseFile.getAbsolutePath()
        );

        createTables();
    }

    private void createTables() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS accounts (
                    uuid TEXT PRIMARY KEY,
                    username TEXT NOT NULL UNIQUE,
                    password_hash TEXT NOT NULL,
                    created_at INTEGER NOT NULL
                )
                """;

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        }
    }

    public Connection getConnection() {
        if (connection == null) {
            throw new IllegalStateException(
                    "Database has not been initialized."
            );
        }

        return connection;
    }

    @Override
    public void close() {
        if (connection == null) {
            return;
        }

        try {
            connection.close();
        } catch (SQLException exception) {
            System.err.println(
                    "Failed to close database: "
                            + exception.getMessage()
            );
        } finally {
            connection = null;
        }
    }
}
