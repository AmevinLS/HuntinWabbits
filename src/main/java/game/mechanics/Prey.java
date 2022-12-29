package game.mechanics;

import java.util.Random;

enum State {
    DEFAULT, MOVING_TO_FOOD, MOVING_TO_WATER, MOVING_TO_HIDEOUT, EATING, DRINKING, HIDING
}

public class Prey extends Animal {
    private class Depleter extends Thread {
        private final static int DEPLETION_TIME = 2000;
        private boolean stopped = false;

        public void finish() {
            stopped = true;
        }

        @Override
        public void run() {
            while (true) {
                if (stopped)
                    break;

                try {
                    Thread.sleep(DEPLETION_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                processWaterLvlChange(-1);
                processFoodLvlChange(-1);
            }
        }
    }

    final private Depleter depleter = new Depleter();

    final private int MAX_HEALTH;
    final private int MAX_WATER_LVL;
    final private int MAX_FOOD_LVL;
    private int waterLvl;
    private final Object waterLvlGuard = new Object();
    private int foodLvl;
    private final Object foodLvlGuard = new Object();
    private State state = State.DEFAULT;
    private final Object stateGuard = new Object();

    private Place targetPlace = null;

    public Prey(Game game, String name, int health, int speed, int strength, String species, Position pos, int maxWaterLvl, int maxFoodLvl) {
        super(game, name, health, speed, strength, species, pos);
        this.MAX_HEALTH = health;
        this.MAX_FOOD_LVL = maxFoodLvl;
        this.MAX_WATER_LVL = maxWaterLvl;
        this.foodLvl = this.MAX_FOOD_LVL;
        this.waterLvl = this.MAX_WATER_LVL;
    }

    public WaterSource selectWaterSource() throws PreyPathInfeasibleException{
        WaterSource bestSrc = null;
        int bestDist = -1;
        for (Place place : game.getPlaces()) {
            if (place instanceof WaterSource currSrc) {
                int currDist = game.getMap().getPreyPath(pos, currSrc.getPos()).size();

                if (bestSrc == null) {
                    bestSrc = currSrc;
                    bestDist = currDist;
                    continue;
                }

                if ((currSrc.numFreeSpaces() != 0) && (currDist < bestDist)) {
                    bestSrc = currSrc;
                    bestDist = currDist;
                }
            }
        }
        return bestSrc;
    }

    public FoodSource selectFoodSource() throws PreyPathInfeasibleException {
        FoodSource bestSrc = null;
        int bestDist = -1;
        for (Place place : game.getPlaces()) {
            if (place instanceof FoodSource currSrc) {
                int currDist = game.getMap().getPreyPath(pos, currSrc.getPos()).size();

                if (bestSrc == null) {
                    bestSrc = currSrc;
                    bestDist = currDist;
                    continue;
                }

                if ((currSrc.numFreeSpaces() != 0) && (currDist < bestDist)) {
                    bestSrc = currSrc;
                    bestDist = currDist;
                }
            }
        }
        return bestSrc;
    }

    public Place selectPlace() throws PreyPathInfeasibleException{
        Place bestPlace = switch (state) {
            case MOVING_TO_FOOD -> new FoodSource(new Position(0, 0), "foo", 1, 1);
            case MOVING_TO_WATER -> new WaterSource(new Position(0, 0), "foo", 1, 1);
            case MOVING_TO_HIDEOUT -> new Hideout(new Position(0, 0), "foo", 1);
            default -> throw new RuntimeException("Prey trying to select a target Place from not a 'MOVING_TO_...' state");
        };
        int bestDist = -1;
        for (Place place : game.getPlaces()) {
            if (place.getClass().equals(bestPlace.getClass())) {
                int currDist = game.getMap().getPreyPath(pos, place.getPos()).size();

                if (bestDist == -1) {
                    bestPlace = place;
                    bestDist = currDist;
                    continue;
                }

                if ((place.numFreeSpaces() != 0) && (currDist < bestDist)) {
                    bestPlace = place;
                    bestDist = currDist;
                }
            }
        }
        return bestPlace;
    }

    public synchronized void decreaseHealth(int amount) {
        this.health -= amount;
        killSelfIfDead();
    }
    public void processWaterLvlChange(int amount) {
        synchronized (waterLvlGuard) {
            if (amount > 0) {
                waterLvl = Integer.min(waterLvl + amount, MAX_WATER_LVL);
            }
            else if (amount < 0) {
                if (waterLvl == 0) {
                    decreaseHealth(1);
                    return;
                }
                waterLvl = Integer.max(waterLvl + amount, 0);
            }
        }
        killSelfIfDead();
    }
    public void processFoodLvlChange(int amount) {
        synchronized (foodLvlGuard) {
            if (amount > 0) {
                foodLvl = Integer.min(foodLvl + amount, MAX_FOOD_LVL);
            }
            else if (amount < 0) {
                if (foodLvl == 0) {
                    decreaseHealth(1);
                    return;
                }
                foodLvl = Integer.max(foodLvl + amount, 0);
            }
        }
        killSelfIfDead();
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

    private synchronized void killSelf() {
        depleter.finish();
        game.removeAnimal(this);
    }

    private synchronized void killSelfIfDead() {
        if (!this.isAlive()) {
            System.out.println("Yo, I'm killing myself");
            this.killSelf();
        }
    }

    private void enterTargetPlace() {
        if (!pos.equals(targetPlace.getPos())) {
            throw new RuntimeException("Trying to enter place, with positions of place and prey not equal");
        }
        targetPlace.enter(this);
        synchronized (stateGuard) {
            state = switch (state) {
                case MOVING_TO_FOOD -> State.EATING;
                case MOVING_TO_WATER -> State.DRINKING;
                case MOVING_TO_HIDEOUT -> State.HIDING;
                default -> throw new RuntimeException("Prey trying to enter Place from not a 'MOVING_TO_...' state");
            };
        }
    }

    @Override
    public void run() {
        depleter.setDaemon(true);
        depleter.start();

        Random rand = new Random();
        while(this.isAlive()) {
//            System.out.println("I am alive! Water: " + waterLvl + ", Food: " + foodLvl);
            try {
                System.out.println("State:" + state);
                Thread.sleep(LOOP_TIME_DELAY / speed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (this) {
                if (state == State.DEFAULT) {
                    if (foodLvl <= Math.ceil(MAX_FOOD_LVL * 0.4)) {
                        // TODO: investigate why drawing stuff changes
                        state = State.MOVING_TO_FOOD;
                    }
                    else if (waterLvl <= Math.ceil(MAX_WATER_LVL*0.4)) {
                        state = State.MOVING_TO_WATER;
                    }
                    else {
//                        state = State.MOVING_TO_HIDEOUT;
                    }

                    if (state == State.MOVING_TO_FOOD || state == State.MOVING_TO_WATER || state == State.MOVING_TO_HIDEOUT) {
                        try {
                            Place place = this.selectPlace();
                            this.currPath = game.getMap().getPreyPath(this.pos, place.getPos());
                            this.targetPlace = place;
                        } catch (PreyPathInfeasibleException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }

                if (state == State.MOVING_TO_FOOD || state == State.MOVING_TO_WATER || state == State.MOVING_TO_HIDEOUT) {
                    System.out.println("About to make step on path");
                    this.makeStepOnPath();
                    if (this.currPath.isEmpty()) {
                        this.currPath = null;
                        this.enterTargetPlace();
                    }
                }

                if (state == State.EATING && foodLvl >= Math.floor(MAX_FOOD_LVL * 0.9)) {
                    this.targetPlace.leave(this);
                    state = State.DEFAULT;
                }
                else if (state == State.DRINKING && waterLvl >= Math.floor(MAX_WATER_LVL * 0.9)) {
                    this.targetPlace.leave(this);
                    state = State.DEFAULT;
                }

                if (state == State.HIDING) {
                    // TODO: do something here
                }
            }

//            try {
//                Thread.sleep(LOOP_TIME_DELAY / speed);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//
//            synchronized (this) {
//                Position newPos = new Position(-1, -1);
//                while (!game.getMap().isValidPos(newPos)) {
//                    int i = rand.nextInt(4);
//                    if (i == 0) {
//                        newPos = pos.shift(-1, 0);
//                    } else if (i == 1) {
//                        newPos = pos.shift(0, 1);
//                    } else if (i == 2) {
//                        newPos = pos.shift(1, 0);
//                    } else {
//                        newPos = pos.shift(0, -1);
//                    }
//                }
//                pos = newPos;
//            }
//
//            if (state == State.DEFAULT && health <= Math.ceil(MAX_HEALTH * 0.2)) {
//                this.state = State.MOVING_TO_FOOD;
//                try {
//                    FoodSource src = this.selectFoodSource();
//                    this.currPath = game.getMap().getPreyPath(this.pos, src.getPos());
//                } catch (PreyPathInfeasibleException e) {
//                    throw new RuntimeException(e);
//                }
//            }
        }
    }

    public int getWaterLvl() {
        return waterLvl;
    }

    public int getFoodLvl() {
        return foodLvl;
    }
}
