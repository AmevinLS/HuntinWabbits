package game.mechanics;

public abstract class Animal implements Runnable{
    final private String name;
    private int health;
    private int speed;
    final private int strength;
    final private String species;
    private Position pos;
    private Path curr_path = null;

    Animal(String name, int health, int speed, int strength, String species, Position pos) {
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.strength = strength;
        this.species = species;
        this.pos = pos;
    }

    public void makeStepOnPath() {
        this.pos = curr_path.getNextPos();
    }

    public Position getPos() {
        return this.pos;
    }
}
