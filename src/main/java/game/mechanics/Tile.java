package game.mechanics;

public abstract class Tile {
    private Position pos;

    Tile(Position p) {
        this.pos = p;
    }

    public Position getPos() {
        return pos;
    }
}
