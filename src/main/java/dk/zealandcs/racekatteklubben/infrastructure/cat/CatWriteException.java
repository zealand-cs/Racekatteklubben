package dk.zealandcs.racekatteklubben.infrastructure.cat;

public class CatWriteException extends RuntimeException {
    public CatWriteException(String message) {
        super(message);
    }
}
