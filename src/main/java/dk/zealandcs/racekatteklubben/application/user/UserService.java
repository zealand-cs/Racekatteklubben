package dk.zealandcs.racekatteklubben.application.user;

import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;
import dk.zealandcs.racekatteklubben.infrastructure.user.IUserRepository;
import dk.zealandcs.racekatteklubben.infrastructure.user.UserWriteException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements IUserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final IUserRepository userRepository;
    //Constructor injection of User Repository
    UserService(IUserRepository repo) { this.userRepository = repo; }

    //List of all user objects
    @Override
    public List<User> allUsers() {
        logger.info("Get all users");
        return userRepository.findAll();
    }

    //Method to find user by the ID
    @Override
    public Optional<User> getUser(int id) {
        logger.info("Get user {}", id);
        return userRepository.findById(id);
    }

    //method to login, checks email and verifies password using Bcrypt
    @Override
    public Optional<User> login(String email, String password) {
        logger.info("Get login {}", email);
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (!BCrypt.checkpw(password, user.get().getPassword())) {
            return Optional.empty();
        }

        return user;
    }

    //Registers a new user after validating the input values, and assigns default role of user
    @Override
    public Optional<User> register(User user) {
        user.setRole(Role.User);
        logger.info("Register user {}", user);

        try {
            if (user.validate()) {
                var registeredUser = userRepository.write(user);
                return Optional.of(registeredUser);
            }
            return Optional.empty();
        } catch (UserWriteException e) {
            logger.error(e.getMessage());
            return Optional.empty();
        }
    }
    //Updates user information, checks for correct permissions... executing user = performnig user, and targetinguser is
    //the user we are changing
    @Override
    public boolean updateUser(User executingUser, User targetUser) {
        logger.info("Update user {}", targetUser);
        if (executingUser.getId() == targetUser.getId() || executingUser.getRole().isAtLeast(Role.Employee)) {
            userRepository.update(targetUser);
            logger.info("Update user successful {}", targetUser);
            return true;
        }
        logger.error("Update user failed ");
        return false;
    }
    //Deletes user, checks for permissions to do so
    @Override
    public boolean deleteUser(User executingUser, User targetUser) {
        logger.info("Delete user initiated {}", targetUser);
        if (executingUser.getId() == targetUser.getId() || executingUser.getRole().isAtLeast(Role.Admin)) {
            userRepository.delete(targetUser.getId());
            logger.info("Delete user successful {}", targetUser);
            return true;
        }
        logger.error("Delete user failed ");
        return false;
    }

    //Updates user password only allowed if admin or is the user itself.
    @Override
    public boolean updatePassword(User executingUser, User targetUser, String password) {
        logger.info("Update password initiated {}", targetUser);
        if (executingUser.getId() == targetUser.getId() || executingUser.getRole().isAtLeast(Role.Admin)) {
            targetUser.setPassword(password);
            userRepository.updatePassword(targetUser);
            logger.info("Update password successful {}", targetUser);
            return true;
        }
        logger.error("Update password failed ");
        return false;
    }

    //Updates another users role, checks for permission
    @Override
    public boolean updateRole(User executingUser, User targetUser, Role role) {
        logger.info("Update role initiated {}", targetUser);
        if (executingUser.getRole().isAtLeast(Role.Admin)) {
            targetUser.setRole(role);
            userRepository.updateRole(targetUser);
            logger.info("Update role successful {}", targetUser);
            return true;
        }
        logger.error("Update role failed ");
        return false;
    }
}
