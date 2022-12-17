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

    @Override
    public boolean equals(Object p) {
        if(p instanceof Position) {
            return (this.x == ((Position)p).x) && (this.y == ((Position)p).y);
        }
        return false;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
