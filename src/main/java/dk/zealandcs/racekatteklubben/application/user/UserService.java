package dk.zealandcs.racekatteklubben.application.user;

import dk.zealandcs.racekatteklubben.domain.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    @Override
    public Optional<User> getUser(int id) {
        return Optional.empty();
    }

    @Override
    public Optional<User> login(String email, String password) {
        return Optional.empty();
    }

    @Override
    public User register(User user) {
        return null;
    }
}
