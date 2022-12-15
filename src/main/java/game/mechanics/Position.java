package game.mechanics;

public class Position {
    final private int x;
    final private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static int distance(Position a, Position b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    public Position shift(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(Position p) {
        return (this.x == p.x) && (this.y == p.y);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
