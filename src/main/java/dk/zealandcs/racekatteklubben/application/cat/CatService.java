package dk.zealandcs.racekatteklubben.application.cat;

import dk.zealandcs.racekatteklubben.domain.Cat;
import dk.zealandcs.racekatteklubben.infrastructure.cat.ICatRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CatService implements ICatService {
    private final ICatRepository catRepository;

    CatService(ICatRepository repo) { this.catRepository = repo; }

    @Override
    public Optional<Cat> getCat(int id) {
        return catRepository.findById(id);
    }

    @Override
    public Optional<Cat> createCat(Cat cat) {
        return Optional.ofNullable(catRepository.write(cat));
    }

    @Override
    public List<Cat> allCats() {
        return catRepository.findAll();
    }
}
