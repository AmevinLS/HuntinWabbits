package game.mechanics;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;


public abstract class Place extends Tile implements PreyVisitable {
    private String name;
    private int maxCapacity;
    Semaphore sem;
    protected List<Prey> occupants = new LinkedList<>();
    protected final Object occupantsGuard = new Object();

    Place(Position p, String name, int capacity) {
        super(p);
        this.name = name;
        this.maxCapacity = capacity;
        this.sem = new Semaphore(capacity);
    }

    public void enter(Prey prey) {
        sem.acquireUninterruptibly();
        synchronized (occupantsGuard) {
            occupants.add(prey);
        }
    }

    public void leave(Prey prey) {
        sem.release();
        synchronized (occupantsGuard) {
            occupants.remove(prey);
        }
    }

    public synchronized void setMaxCapacity(int newCapacity) {
        this.maxCapacity = newCapacity;
        if(!sem.hasQueuedThreads()) {
            sem = new Semaphore(maxCapacity);
        }
        else {
            throw new RuntimeException("Attempting to change Place capacity with occupants still inside");
        }
    }

    public synchronized int numFreeSpaces() {
        return sem.availablePermits();
    }

    public List<Prey> getOccupants() {
        return occupants;
    }

    public Object getOccupantsGuard() {
        return occupants;
    }
}
