package dk.zealandcs.racekatteklubben.application.cat;

import dk.zealandcs.racekatteklubben.domain.Cat;

import java.util.List;
import java.util.Optional;

public interface ICatService {
    Optional<Cat> getCat(int id);
    Optional<Cat> createCat(Cat cat);
    List<Cat> allCats();
}
