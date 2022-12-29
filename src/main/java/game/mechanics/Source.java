package game.mechanics;

public abstract class Source extends Place implements Runnable{
    int replenishSpeed;
    protected final static int REPLENISH_TIME = 500;

    Source(Position p, String name, int capacity, int replenishSpeed) {
        super(p, name, capacity);
        this.replenishSpeed = replenishSpeed;
    }
}
