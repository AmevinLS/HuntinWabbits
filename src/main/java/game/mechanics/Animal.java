package game.mechanics;

public abstract class Animal implements Runnable{
    final protected Game game;
    final protected String name;
    protected int health;
    protected int speed;
    final protected int strength;
    final protected String species;
    protected Position pos;
    protected Path currPath = null;

    protected final static int LOOP_TIME_DELAY = 1000;
    protected final static int RESOURCE_DRAIN_TIME = 2000;

    Animal(Game game, String name, int health, int speed, int strength, String species, Position pos) {
        this.game = game;
        this.name = name;
        this.health = health;
        this.speed = speed;
        this.strength = strength;
        this.species = species;
        this.pos = pos;
    }

    public void makeStepOnPath() {
        this.pos = currPath.getNextPos();
    }

    public Position getPos() {
        return this.pos;
    }

    public Path getCurrPath() {
        return this.currPath;
    }

    public int getHealth() {
        return this.health;
    }
}
