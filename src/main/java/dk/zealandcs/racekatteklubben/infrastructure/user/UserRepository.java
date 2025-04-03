package dk.zealandcs.racekatteklubben.infrastructure.user;

import dk.zealandcs.racekatteklubben.config.DatabaseConfig;
import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {

    @Autowired
    private DatabaseConfig databaseConfig;

    @Override
    public User write(User user) throws UserWriteException {
        String sql = "INSERT INTO users (name, email, password, dateOfBirth, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Hash password and assign it to user
            String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(passwordHash);

            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getPassword());
            stmt.setDate(4, user.getDateOfBirth());
            stmt.setString(5, user.getRole().name());
            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                    return user;
                }
            }
            throw new UserWriteException("Something went wrong when registering");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UserWriteException("Email already in use");
        }
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT id, name, email, password, dateOfBirth, role FROM users WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return userFromResultSet(rs);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password, dateOfBirth, role FROM users WHERE email = ?";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, email);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return userFromResultSet(rs);
            }
            return Optional.empty();
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT id, name, email, password, dateOfBirth, role FROM users";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            var rs = stmt.executeQuery();
            var users = new ArrayList<User>();
            while (rs.next()) {
                var user = userFromResultSet(rs);
                user.ifPresent(users::add);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO correct exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, dateOfBirth = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setDate(3, user.getDateOfBirth());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO correct exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updatePassword(User user) throws UserWriteException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            // Hash password and assign it to user
            String passwordHash = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
            user.setPassword(passwordHash);

            stmt.setString(1, user.getPassword());
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO correct exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateRole(User user) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, user.getRole().name());
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO correct exception
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (var conn = databaseConfig.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    Optional<User> userFromResultSet(ResultSet rs) {
        try {
            var user = new User();
            user.setId(rs.getInt("id"));
            user.setName(rs.getString("name"));
            user.setEmail(rs.getString("email"));
            user.setPassword(rs.getString("password"));
            user.setDateOfBirth(rs.getDate("dateOfBirth"));
            user.setRole(Role.valueOf(rs.getString("role")));
            return Optional.of(user);
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
