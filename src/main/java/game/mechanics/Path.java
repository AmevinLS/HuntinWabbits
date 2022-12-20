package game.mechanics;

import java.util.ArrayList;
import java.util.List;

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

    public Position popLastPosition() {
        return posits.remove(posits.size()-1);
    }

    public boolean isEmpty() {
        return posits.isEmpty();
    }

    public boolean contains(Position pos) {
        return posits.contains(pos);
    }

    public int size() {
        return posits.size();
    }

    public Position get(int ind) {
        return posits.get(ind);
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
