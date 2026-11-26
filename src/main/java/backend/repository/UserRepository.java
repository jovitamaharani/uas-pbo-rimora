package backend.repository;

import backend.db.DBHelper;
import backend.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserRepository implements CrudRepository<User, UUID> {

    @Override
    public List<User> findAll() throws SQLException {
        String sql = """
                SELECT u.id, u.role_id, r.name AS role_name, u.student_class_id, u.name, u.email,
                       u.password, u.created_at, u.telepon, u.photo_path, u.nim, u.academic_year_id
                FROM users u
                LEFT JOIN roles r ON u.role_id = r.id
                ORDER BY u.created_at DESC NULLS LAST, u.name ASC
                """;
        List<User> users = new ArrayList<>();

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapRow(rs));
            }
        }
        return users;
    }

    @Override
    public Optional<User> findById(UUID id) throws SQLException {
        String sql = """
                SELECT u.id, u.role_id, r.name AS role_name, u.student_class_id, u.name, u.email,
                       u.password, u.created_at, u.telepon, u.photo_path, u.nim, u.academic_year_id
                FROM users u
                LEFT JOIN roles r ON u.role_id = r.id
                WHERE u.id = ?
                """;

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean insert(User entity) throws SQLException {
        String sql = """
                INSERT INTO users (
                    role_id, student_class_id, name, email, password,
                    telepon, photo_path, nim, academic_year_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                RETURNING id, created_at
                """;

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, entity.getRoleId());
            stmt.setObject(2, entity.getStudentClassId());
            stmt.setString(3, entity.getName());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getPassword());
            stmt.setString(6, entity.getTelepon());
            stmt.setString(7, entity.getPhotoPath());
            stmt.setString(8, entity.getNim());
            stmt.setObject(9, entity.getAcademicYearId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getObject("id", UUID.class));
                    Timestamp ts = rs.getTimestamp("created_at");
                    entity.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean update(User entity) throws SQLException {
        String sql = """
                UPDATE users
                SET role_id = ?, student_class_id = ?, name = ?, email = ?, password = ?,
                    telepon = ?, photo_path = ?, nim = ?, academic_year_id = ?
                WHERE id = ?
                """;

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, entity.getRoleId());
            stmt.setObject(2, entity.getStudentClassId());
            stmt.setString(3, entity.getName());
            stmt.setString(4, entity.getEmail());
            stmt.setString(5, entity.getPassword());
            stmt.setString(6, entity.getTelepon());
            stmt.setString(7, entity.getPhotoPath());
            stmt.setString(8, entity.getNim());
            stmt.setObject(9, entity.getAcademicYearId());
            stmt.setObject(10, entity.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getObject("id", UUID.class));
        user.setRoleId(rs.getObject("role_id", UUID.class));
        user.setRoleName(rs.getString("role_name"));
        user.setStudentClassId(rs.getObject("student_class_id", UUID.class));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));

        Timestamp ts = rs.getTimestamp("created_at");
        user.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

        user.setTelepon(rs.getString("telepon"));
        user.setPhotoPath(rs.getString("photo_path"));
        user.setNim(rs.getString("nim"));
        user.setAcademicYearId(rs.getObject("academic_year_id", UUID.class));
        return user;
    }
}