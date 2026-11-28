package backend.db;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBHelper {
    private static final Dotenv DOTENV = Dotenv.configure()
            .directory(System.getProperty("user.dir"))
            .filename(".env.local")
            .ignoreIfMissing()
            .load();

    private static final String DB_URL = DOTENV.get("DB_URL", DOTENV.get("SUPABASE_URL"));
    private static final String DB_USER = DOTENV.get("DB_USER");
    private static final String DB_PASSWORD = DOTENV.get("DB_PASSWORD");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Driver PostgreSQL (org.postgresql.Driver) tidak ditemukan di classpath.", e);
        }
    }

    private DBHelper() {     
    }

    public static Connection getConnection() throws SQLException {
        if (DB_URL == null || DB_URL.isBlank()) {
            throw new SQLException("Konfigurasi DB_URL / SUPABASE_URL di .env.local belum diatur.");
        }

        if (DB_USER != null && !DB_USER.isBlank()) {
            Properties props = new Properties();
            props.setProperty("user", DB_USER);
            if (DB_PASSWORD != null) {
                props.setProperty("password", DB_PASSWORD);
            }
            return DriverManager.getConnection(DB_URL, props);
        }

        return DriverManager.getConnection(DB_URL);
    }
}