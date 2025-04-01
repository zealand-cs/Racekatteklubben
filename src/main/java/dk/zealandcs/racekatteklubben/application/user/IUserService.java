package dk.zealandcs.racekatteklubben.application.user;

import dk.zealandcs.racekatteklubben.domain.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface IUserService {
    Optional<User> getUser(int id);
    Optional<User> login(String email, String password);
    User register(User user);
}
