package game.mechanics;

import java.util.Random;

enum State {
    DEFAULT, MOVING_TO_FOOD, MOVING_TO_WATER, MOVING_TO_HIDEOUT, EATING, DRINKING, HIDING
}

public class Prey extends Animal {
    final private int MAX_WATER_LVL;
    final private int MAX_FOOD_LVL;
    private int waterLvl;
    private int foodLvl;
    private State state = State.DEFAULT;

    public Prey(Game game, String name, int health, int speed, int strength, String species, Position pos, int maxWaterLvl, int maxFoodLvl) {
        super(game, name, health, speed, strength, species, pos);
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

    public synchronized boolean receiveAttack(Predator pred) {
        if (pos.equals(pred.getPos())) {
            if (strength < pred.strength) {
                health -= (pred.strength - strength);
                if (!this.isAlive()) {
                    this.killSelf();
                }
            }
            System.out.println("Aw, damn, I got attacked! HP left: " + health);
            return true;
        }
        return false;
    }

    public void reproduce() {
        // TODO
    }

    public synchronized boolean isAlive() {
        return (health > 0);
    }

    public void killSelf() {
        game.removeAnimal(this);
        for (Animal anim : game.getAnimals()) {
            if (anim instanceof Predator) {
                ((Predator) anim).removeTarget(this);
            }
        }
    }

    @Override
    public void run() {
        Random rand = new Random();
        while(this.isAlive()) {
            System.out.println("I am alive!");
            try {
                Thread.sleep(TIME_DELTA / speed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            synchronized (this) {
                Position newPos = new Position(-1, -1);
                while (!game.getMap().isValidPos(newPos)) {
                    int i = rand.nextInt(4);
                    if (i == 0) {
                        newPos = pos.shift(-1, 0);
                    } else if (i == 1) {
                        newPos = pos.shift(0, 1);
                    } else if (i == 2) {
                        newPos = pos.shift(1, 0);
                    } else {
                        newPos = pos.shift(0, -1);
                    }
                }
                pos = newPos;
            }
        }
    }
}
