package dk.zealandcs.racekatteklubben.application.user;

import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;
import dk.zealandcs.racekatteklubben.infrastructure.user.IUserRepository;
import dk.zealandcs.racekatteklubben.infrastructure.user.UserWriteException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {
    private final IUserRepository userRepository;

    UserService(IUserRepository repo) { this.userRepository = repo; }

    @Override
    public Optional<User> getUser(int id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> login(String email, String password) {
        var user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (!BCrypt.checkpw(password, user.get().getPassword())) {
            return Optional.empty();
        }

        return user;
    }

    @Override
    public Optional<User> register(User user) {
        user.setRole(Role.User);

        try {
            if (user.validate()) {
                var registeredUser = userRepository.write(user);
                return Optional.of(registeredUser);
            }
            return Optional.empty();
        } catch (UserWriteException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
