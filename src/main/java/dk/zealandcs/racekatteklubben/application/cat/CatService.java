package dk.zealandcs.racekatteklubben.application.cat;

import dk.zealandcs.racekatteklubben.domain.Cat;
import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;
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
    public List<Cat> getCatsByOwner(int ownerId) {
        return catRepository.findByUserId(ownerId);
    }

    @Override
    public Optional<Cat> createCat(Cat cat) {
        return Optional.ofNullable(catRepository.write(cat));
    }

    @Override
    public boolean editCat(User executingUser, Cat cat) {
        if (executingUser.getId() == cat.getOwnerId() || executingUser.getRole().isAtLeast(Role.Employee)) {
            catRepository.update(cat);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteCat(User executingUser, Cat cat) {
        if (executingUser.getId() == cat.getOwnerId() || executingUser.getRole().isAtLeast(Role.Employee)) {
            catRepository.delete(cat.getId());
            return true;
        }
        return false;
    }

    @Override
    public List<Cat> allCats() {
        return catRepository.findAll();
    }
}
