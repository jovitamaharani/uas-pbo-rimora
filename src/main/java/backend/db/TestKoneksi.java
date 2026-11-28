package backend.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TestKoneksi {
    public static void main(String[] args) {
        String sql = "SELECT current_database(), current_user, version()";

        try (Connection conn = DBHelper.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                System.out.println("Koneksi BERHASIL!");
                System.out.println("Database : " + rs.getString(1));
                System.out.println("User     : " + rs.getString(2));
                System.out.println("Version  : " + rs.getString(3));
            }
        } catch (SQLException e) {
            System.out.println("Koneksi GAGAL!");
            System.out.println("SQLState : " + e.getSQLState());
            System.out.println("Pesan    : " + e.getMessage());
            e.printStackTrace();
        }
    }
}