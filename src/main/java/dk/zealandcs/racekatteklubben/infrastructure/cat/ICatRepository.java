package dk.zealandcs.racekatteklubben.infrastructure.cat;

import dk.zealandcs.racekatteklubben.domain.Cat;

import java.util.List;
import java.util.Optional;

public interface ICatRepository {
    Cat write(Cat cat);
    Optional<Cat> findById(int id);
    Optional<Cat> findByUserId(int id);
    List<Cat> findAll();
    void update(Cat cat);
    void delete(int id);
}
