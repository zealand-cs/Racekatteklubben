package dk.zealandcs.racekatteklubben.infrastructure.user;

public class UserWriteException extends RuntimeException {
    public UserWriteException(String message) {
        super(message);
    }
}
