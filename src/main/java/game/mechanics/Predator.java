package game.mechanics;

enum Mode {
    HUNTING, RELAXING
}

public class Predator extends Animal {
    private Mode mode = Mode.HUNTING;

    Predator(String name, int health, int speed, int strength, String species, Position pos) {
        super(name, health, speed, strength, species, pos);
    }

    public Prey selectPrey() {
        // TODO
        return null;
    }

    public void attack(Prey prey) {
        // TODO
    }

    public void run() {
        // TODO
    }
}
