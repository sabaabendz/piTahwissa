package services;

import entities.User;
import utils.MyDatabase;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public class UserService implements IService<User> {

    private Connection connection;

    public UserService() {
        connection = MyDatabase.getInstance().getConnection();
        System.out.println("✅ UserService initialisé");
    }

    @Override
    public void ajouter(User user) throws SQLException {
        System.out.println("📝 UserService.ajouter() - Début");
        System.out.println("   Utilisateur: " + user.getFirstName() + " " + user.getLastName());
        System.out.println("   Email: " + user.getEmail());
        System.out.println("   Phone: " + user.getPhone());
        System.out.println("   City: " + user.getCity());
        System.out.println("   Country: " + user.getCountry());
        System.out.println("   Role: " + user.getRole() + " (ID: " + getRoleId(user.getRole()) + ")");

        String sql = "INSERT INTO user (email, password, first_name, last_name, phone, city, country, role_id, is_verified, is_active) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        System.out.println("📝 SQL: " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getFirstName());
            ps.setString(4, user.getLastName());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getCity());
            ps.setString(7, user.getCountry());
            ps.setInt(8, getRoleId(user.getRole()));
            ps.setBoolean(9, user.isVerified());
            ps.setBoolean(10, user.isActive());

            System.out.println("🔄 Exécution de la requête SQL...");
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ Utilisateur ajouté avec succès! Lignes affectées: " + rowsAffected);
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de l'ajout:");
            System.err.println("   Code: " + e.getErrorCode());
            System.err.println("   SQLState: " + e.getSQLState());
            System.err.println("   Message: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public void update(User user) throws SQLException {
        String sql = "UPDATE user SET email = ?, first_name = ?, last_name = ?, role_id = ? WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setInt(4, getRoleId(user.getRole()));
            ps.setInt(5, user.getId());
            ps.executeUpdate();
        }
        System.out.println("✏️  Utilisateur mis à jour.");
    }

    @Override
    public void supprimer(int id) throws SQLException {
        String sql = "DELETE FROM user WHERE id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
        System.out.println("🗑️  Utilisateur supprimé.");
    }

    @Override
    public List<User> read() throws SQLException {
        String sql = "SELECT u.*, r.name as role_name FROM user u JOIN role r ON u.role_id = r.id";

        List<User> users = new ArrayList<>();
        try (Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(sql)) {

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
        }
        System.out.println("📖 " + users.size() + " utilisateur(s) trouvé(s).");
        return users;
    }

    private int getRoleId(String roleName) {
        if (roleName == null) return 1;

        return switch (roleName.toUpperCase()) {
            case "USER", "VOYAGEUR" -> 1;
            case "AGENT", "GUIDE" -> 2;
            case "ADMIN" -> 3;
            default -> 1;
        };
    }

    public User findByEmail(String email) throws SQLException {
        System.out.println("🔍 Recherche de l'utilisateur par email: " + email);
        String sql = "SELECT u.*, r.name as role_name FROM user u JOIN role r ON u.role_id = r.id WHERE u.email = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setPhone(rs.getString("phone"));
                    user.setCity(rs.getString("city"));
                    user.setCountry(rs.getString("country"));
                    user.setRole(rs.getString("role_name"));
                    user.setVerified(rs.getBoolean("is_verified"));
                    user.setActive(rs.getBoolean("is_active"));

                    System.out.println("✅ Utilisateur trouvé: " + user.getFirstName() + " " + user.getLastName());
                    System.out.println("   ID: " + user.getId());
                    System.out.println("   Email: " + user.getEmail());
                    System.out.println("   Rôle: " + user.getRole());
                    System.out.println("   Vérifié: " + user.isVerified());
                    System.out.println("   Actif: " + user.isActive());

                    return user;
                } else {
                    System.out.println("❌ Aucun utilisateur trouvé avec l'email: " + email);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur SQL lors de la recherche:");
            System.err.println("   Message: " + e.getMessage());
            throw e;
        }
        return null;
    }

    public void saveFaceEmbedding(int userId, double[] embedding) throws SQLException {
        if (embedding == null || embedding.length == 0) {
            return;
        }
        String sql = "UPDATE user SET face_embedding = ?, face_enrolled_at = NOW() WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setBytes(1, toBytes(embedding));
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public double[] loadFaceEmbedding(int userId) throws SQLException {
        String sql = "SELECT face_embedding FROM user WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    byte[] data = rs.getBytes("face_embedding");
                    if (data == null || data.length == 0) {
                        return null;
                    }
                    return toDoubles(data);
                }
            }
        }
        return null;
    }

    private byte[] toBytes(double[] values) {
        ByteBuffer buffer = ByteBuffer.allocate(values.length * Double.BYTES);
        DoubleBuffer doubleBuffer = buffer.asDoubleBuffer();
        doubleBuffer.put(values);
        return buffer.array();
    }

    private double[] toDoubles(byte[] data) {
        DoubleBuffer buffer = ByteBuffer.wrap(data).asDoubleBuffer();
        double[] values = new double[buffer.remaining()];
        buffer.get(values);
        return values;
    }
}