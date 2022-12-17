package game.mechanics;

public class InvalidMapException extends Exception {
    public InvalidMapException() {
        super();
    }

    public InvalidMapException(String str) {
        super(str);
    }

    public InvalidMapException(String str, Exception cause) {
        super(str, cause);
    }
}
