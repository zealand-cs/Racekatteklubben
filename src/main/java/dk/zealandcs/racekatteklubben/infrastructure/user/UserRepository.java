package dk.zealandcs.racekatteklubben.infrastructure.user;

import dk.zealandcs.racekatteklubben.config.DatabaseConfig;
import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository implements IUserRepository {
    private static final Logger logger = LoggerFactory.getLogger(UserRepository.class);
    @Autowired
    private DatabaseConfig databaseConfig;

    //Inserts new user into the database, hashes the password before storing
    @Override
    public User write(User user) throws UserWriteException {
        String sql = "INSERT INTO users (name, email, password, dateOfBirth, role) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = databaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            logger.info("Attempting to register new user with email {} ", user.getEmail());
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
                    logger.info("Successfully registered new user with email {} ", user.getEmail());
                    return user;
                }
            }
            logger.error("Failed to register new user with email {} ", user.getEmail());
            throw new UserWriteException("Something went wrong when registering");
        } catch (SQLException e) {
            logger.error("Error while trying to register new user: {}", e.getMessage());
            throw new UserWriteException("Email already in use");
        }
    }

    //Retrieves user by ID
    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT id, name, email, password, dateOfBirth, role FROM users WHERE id = ?";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, id);
            logger.info("Attempting to find user with id {} ", id);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return userFromResultSet(rs);
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error while trying to find user with id {} ", id);
            return Optional.empty();
        }
    }
    //Retrieves user by Email
    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT id, name, email, password, dateOfBirth, role FROM users WHERE email = ?";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, email);
            logger.info("Attempting to find user with email {} ", email);
            var rs = stmt.executeQuery();
            if (rs.next()) {
                return userFromResultSet(rs);
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error while trying to find user with email {} ", email);
            return Optional.empty();
        }
    }

    //Fetches all users in the system
    @Override
    public List<User> findAll() {
        String sql = "SELECT id, name, email, password, dateOfBirth, role FROM users";
        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            logger.info("Attempting to find all users");
            var rs = stmt.executeQuery();
            var users = new ArrayList<User>();
            while (rs.next()) {
                var user = userFromResultSet(rs);
                user.ifPresent(users::add);
            }
            return users;
        } catch (SQLException e) {
            logger.error("Error fetching all users {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Updates user info
    @Override
    public void update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, dateOfBirth = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setDate(3, user.getDateOfBirth());
            stmt.setInt(4, user.getId());
            stmt.executeUpdate();
            logger.info("Successfully updated user with id {} ", user.getId());
        } catch (SQLException e) {
            logger.error("Error updating user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Updates user password after hashing it
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
            logger.info("Password updated for User with id {} ", user.getId());
        } catch (SQLException e) {
            logger.error("Error updating password for User {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    //Updates user roles
    @Override
    public void updateRole(User user) {
        String sql = "UPDATE users SET role = ? WHERE id = ?";

        try (Connection conn = databaseConfig.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setString(1, user.getRole().name());
            stmt.setInt(2, user.getId());
            stmt.executeUpdate();
            logger.info("Role updated for User with id {} ", user.getId());
        } catch (SQLException e) {
            logger.error("Error updating role for user {}: {}", user.getId(), e.getMessage());
            throw new RuntimeException(e);
        }
    }
    //Deletes user by ID
    @Override
    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (var conn = databaseConfig.getConnection(); var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            logger.info("Deleted user with id {} ", id);
        } catch (SQLException e) {
            logger.error("Error deleting user ID {} : {}", id, e.getMessage());
        }
    }

    //Converts resultset to User object
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
            logger.error("Error mapping user from resultset {}", e.getMessage());
            return Optional.empty();
        }
    }
}
