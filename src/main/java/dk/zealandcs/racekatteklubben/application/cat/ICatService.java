package dk.zealandcs.racekatteklubben.application.cat;

import dk.zealandcs.racekatteklubben.domain.Cat;
import dk.zealandcs.racekatteklubben.domain.User;

import java.util.List;
import java.util.Optional;

public interface ICatService {
    List<Cat> allCats();
    Optional<Cat> getCat(int id);
    Optional<Cat> createCat(Cat cat);
    boolean editCat(User executingUser, Cat cat);
    boolean deleteCat(User executingUser, Cat cat);
}
