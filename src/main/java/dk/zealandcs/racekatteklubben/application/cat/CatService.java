package dk.zealandcs.racekatteklubben.application.cat;

import dk.zealandcs.racekatteklubben.domain.Cat;
import dk.zealandcs.racekatteklubben.infrastructure.cat.ICatRepository;
import dk.zealandcs.racekatteklubben.infrastructure.user.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CatService implements ICatService {
    private final ICatRepository catRepository;

    CatService(ICatRepository repo) { this.catRepository = repo; }

    @Override
    public Optional<Cat> getCat(int id) {
        return Optional.empty();
    }
}
