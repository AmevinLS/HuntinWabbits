package game.mechanics;

import java.util.List;
import java.util.concurrent.Semaphore;


public abstract class Place extends Tile {
    private String name;
    private int maxCapacity;
    Semaphore sem;

    Place(Position p, String name, int capacity) {
        super(p);
        this.name = name;
        this.maxCapacity = capacity;
        this.sem = new Semaphore(capacity);
    }

    public void enter() {
        sem.acquireUninterruptibly();
    }

    public void leave() {
        sem.release();
    }
}
