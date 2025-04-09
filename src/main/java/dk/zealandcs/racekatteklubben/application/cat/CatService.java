package dk.zealandcs.racekatteklubben.application.cat;

import dk.zealandcs.racekatteklubben.domain.Cat;
import dk.zealandcs.racekatteklubben.domain.Role;
import dk.zealandcs.racekatteklubben.domain.User;
import dk.zealandcs.racekatteklubben.infrastructure.cat.ICatRepository;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Optional;

@Service
public class CatService implements ICatService {
    private static final Logger logger = LoggerFactory.getLogger(CatService.class);
    private final ICatRepository catRepository;

    //Constructor injection of repository dependency
    CatService(ICatRepository repo) { this.catRepository = repo; }

    //Fetch cat by ID
    @Override
    public Optional<Cat> getCat(int id) {
        logger.info("Get cat {}", id);
        return catRepository.findById(id);
    }

    //Fetches all cats for a owner
    @Override
    public List<Cat> getCatsByOwner(int ownerId) {
        logger.info("Get cats by owner {}", ownerId);
        return catRepository.findByUserId(ownerId);
    }

    //Creates cat
    @Override
    public Optional<Cat> createCat(Cat cat) {
        logger.info("Create cat {}", cat);
        Cat result = catRepository.write(cat);
        if (result != null) {
            logger.info("Saved cat {}", result);
        } else {
            logger.info("Failed to save cat {}", cat);
        }
        return Optional.ofNullable(result);
    }

    //Edit a cat's information if user is owner or has rights to do so
    @Override
    public boolean editCat(User executingUser, Cat cat) {
        logger.info("Edit cat {}", cat);
        if (executingUser.getId() == cat.getOwnerId() || executingUser.getRole().isAtLeast(Role.Employee)) {
            catRepository.update(cat);
            logger.info("Edited cat {}", cat);
            return true;
        }
        logger.warn("Edit cat {} is not an employee", cat);
        return false;
    }

    //Checks for owner and rights(rank role) when deleting.
    @Override
    public boolean deleteCat(User executingUser, Cat cat) {
        logger.info("Delete cat {}", cat);
        if (executingUser.getId() == cat.getOwnerId() || executingUser.getRole().isAtLeast(Role.Employee)) {
            catRepository.delete(cat.getId());
            logger.info("Deleted cat {}", cat);
            return true;
        }
        logger.warn("Delete cat {} is not an employee", cat);
        return false;
    }

    @Override
    public List<Cat> allCats() {
        logger.info("Get all cats");
        return catRepository.findAll();
    }
}
