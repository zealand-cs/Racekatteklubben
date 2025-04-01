package dk.zealandcs.racekatteklubben.infrastructure.user;

import dk.zealandcs.racekatteklubben.domain.User;

import java.util.List;
import java.util.Optional;

public interface IUserRepository {
    User write(User user) throws UserWriteException;
    Optional<User> findById(int id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void update(User user);
    void delete(int id);
}
