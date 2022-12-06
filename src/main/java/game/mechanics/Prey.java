package game.mechanics;

enum State {
    DEFAULT, MOVING_TO_FOOD, MOVING_TO_WATER, MOVING_TO_HIDEOUT, EATING, DRINKING, HIDING
}

public class Prey extends Animal {
    final private int MAX_WATER_LVL;
    final private int MAX_FOOD_LVL;
    private int waterLvl;
    private int foodLvl;
    private State state = State.DEFAULT;

    Prey(String name, int health, int speed, int strength, String species, Position pos, int maxWaterLvl, int maxFoodLvl) {
        super(name, health, speed, strength, species, pos);
        this.MAX_FOOD_LVL = maxFoodLvl;
        this.MAX_WATER_LVL = maxWaterLvl;
        this.foodLvl = this.MAX_FOOD_LVL;
        this.waterLvl = this.MAX_WATER_LVL;
    }

    public WaterSource selectWaterSource() {
        // TODO
        return null;
    }

    public FoodSource selectFoodSource() {
        // TODO
        return null;
    }

    public void receiveAttack() {
        // TODO
    }

    public void reproduce() {
        // TODO
    }

    public void run() {
        // TODO
    }
}
