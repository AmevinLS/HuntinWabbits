package game.mechanics;

public abstract class Source extends Place {
    int replenishSpeed;

    Source(Position p, String name, int capacity, int replenishSpeed) {
        super(p, name, capacity);
        this.replenishSpeed = replenishSpeed;
    }

}
