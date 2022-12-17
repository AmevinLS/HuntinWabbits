package game.mechanics;

public class PreyPathInfeasibleException extends Exception {
    public PreyPathInfeasibleException() {
        super();
    }

    public PreyPathInfeasibleException(String str) {
        super(str);
    }

    public PreyPathInfeasibleException(String str, Exception cause) {
        super(str, cause);
    }
}
