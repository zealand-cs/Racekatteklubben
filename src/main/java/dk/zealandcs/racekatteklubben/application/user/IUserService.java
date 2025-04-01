package dk.zealandcs.racekatteklubben.application.user;

import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> getUser(int id);
    Optional<User> login(String email, String password);
    Optional<User> register(User user);
    boolean deleteUser(User executingUser, User targetUser);
    boolean updateUser(User executingUser, User targetUser);
    boolean updatePassword(User executingUser, User targetUser, String password);
    boolean updateRole(User executingUser, User targetUser, Role role);
}
