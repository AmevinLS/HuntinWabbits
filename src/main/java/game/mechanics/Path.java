package game.mechanics;

import java.util.List;
import game.mechanics.Position;

public class Path {
    List<Position> posits;

    public void append(Position p) {
        posits.add(p);
    }

    public void prepend(Position p) {
        posits.add(0, p);
    }

    public Position getNextPos() {
        return posits.get(0);
    }
}
