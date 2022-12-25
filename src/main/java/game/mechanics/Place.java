package game.mechanics;

import java.util.List;
import java.util.concurrent.Semaphore;


public abstract class Place extends Tile implements PreyVisitable{
    private String name;
    private int maxCapacity;
    Semaphore sem;

    Place(Position p, String name, int capacity) {
        super(p);
        this.name = name;
        this.maxCapacity = capacity;
        this.sem = new Semaphore(capacity);
    }

    public synchronized void enter() {
        sem.acquireUninterruptibly();
    }

    public synchronized void leave() {
        sem.release();
    }

    public void setMaxCapacity(int newCapacity) {
        this.maxCapacity = newCapacity;
        // TODO: Make this more error-proof
    }

    public synchronized int numFreeSpaces() {
        return sem.availablePermits();
    }
}
