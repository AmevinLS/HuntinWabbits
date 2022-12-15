package game.mechanics;

import java.util.ArrayList;
import java.util.List;
import game.mechanics.Position;

public class Path {
    List<Position> posits;

    public Path() {
        posits = new ArrayList<Position>();
    }

    public void append(Position p) {
        posits.add(p);
    }

    public void prepend(Position p) {
        posits.add(0, p);
    }

    public Position getNextPos() {
        return posits.get(0);
    }

    public Position popNextPos() {
        return posits.remove(0);
    }

    public boolean isEmpty() {
        return posits.isEmpty();
    }

    @Override
    public String toString() {
        String res = "[ ";
        for(Position pos : posits) {
            res += pos + " ";
        }
        res += "]";
        return res;
    }
}
