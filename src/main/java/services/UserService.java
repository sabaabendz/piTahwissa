package services;

import entities.User;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService implements IService<User> {

    private Connection connection;

    public UserService() {
        connection = MyDatabase.getInstance().getConnection();
        System.out.println("✅ UserService initialisé");
    }

    @Override
    public void ajouter(User user) throws SQLException {
        System.out.println("📝 Ajout de l'utilisateur: " + user.getFirstName() + " " + user.getLastName());

        String sql = "INSERT INTO user (email, password, first_name, last_name, role_id) VALUES (?, ?, ?, ?, ?)";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getPassword());
        ps.setString(3, user.getFirstName());
        ps.setString(4, user.getLastName());

        int roleId = getRoleId(user.getRole());
        ps.setInt(5, roleId);

        ps.executeUpdate();
        ps.close();
        System.out.println("✅ Utilisateur ajouté avec succès.");
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET email = ?, first_name = ?, last_name = ?, role_id = ? WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, user.getEmail());
        ps.setString(2, user.getFirstName());
        ps.setString(3, user.getLastName());

        int roleId = getRoleId(user.getRole());
        ps.setInt(4, roleId);
        ps.setInt(5, user.getId());

        ps.executeUpdate();
        ps.close();
        System.out.println("✏️  Utilisateur mis à jour.");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setInt(1, id);
        ps.executeUpdate();
        ps.close();
        System.out.println("🗑️  Utilisateur supprimé.");
    }

    @Override
    public List<User> read() throws SQLException {
        String sql = "SELECT u.*, r.name as role_name FROM user u JOIN role r ON u.role_id = r.id";

        Statement statement = connection.createStatement();
        ResultSet rs = statement.executeQuery(sql);

        List<User> users = new ArrayList<>();
        while (rs.next()) {
            User user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setPhone(rs.getString("phone"));
            user.setRole(rs.getString("role_name"));
            user.setVerified(rs.getBoolean("is_verified"));
            user.setActive(rs.getBoolean("is_active"));

            Timestamp createdAt = rs.getTimestamp("created_at");
            if (createdAt != null) {
                user.setCreatedAt(createdAt.toLocalDateTime());
            }

            users.add(user);
        }

        rs.close();
        statement.close();
        System.out.println("📖 " + users.size() + " utilisateur(s) trouvé(s).");
        return users;
    }

    private int getRoleId(String roleName) {
        if (roleName == null) return 1;

        return switch (roleName.toUpperCase()) {
            case "USER" -> 1;
            case "AGENT" -> 2;
            case "ADMIN" -> 3;
            default -> 1;
        };
    }

    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT u.*, r.name as role_name FROM user u JOIN role r ON u.role_id = r.id WHERE u.email = ?";

        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();

        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getInt("id"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setRole(rs.getString("role_name"));
        }

        rs.close();
        ps.close();
        return user;
    }
}