package backend.repository;

import backend.db.DBHelper;
import backend.model.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class RoleRepository implements CrudRepository<Role, UUID> {

    @Override
    public List<Role> findAll() throws SQLException {
        String sql = "SELECT id, name FROM roles ORDER BY name ASC";
        List<Role> roles = new ArrayList<>();

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                roles.add(mapRow(rs));
            }
        }
        return roles;
    }

    @Override
    public Optional<Role> findById(UUID id) throws SQLException {
        String sql = "SELECT id, name FROM roles WHERE id = ?";

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
    public boolean insert(Role entity) throws SQLException {
        String sql = "INSERT INTO roles (name) VALUES (?) RETURNING id";

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    entity.setId(rs.getObject("id", UUID.class));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean update(Role entity) throws SQLException {
        String sql = "UPDATE roles SET name = ? WHERE id = ?";

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, entity.getName());
            stmt.setObject(2, entity.getId());

            return stmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(UUID id) throws SQLException {
        String sql = "DELETE FROM roles WHERE id = ?";

        try (Connection conn = DBHelper.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    private Role mapRow(ResultSet rs) throws SQLException {
        Role role = new Role();
        role.setId(rs.getObject("id", UUID.class));
        role.setName(rs.getString("name"));
        return role;
    }
}