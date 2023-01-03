package game.mechanics;

import java.util.Date;
import java.util.Random;

enum State {
    DEFAULT, MOVING_TO_FOOD, MOVING_TO_WATER, MOVING_TO_HIDEOUT, EATING, DRINKING, HIDING
}

public class Prey extends Animal {
    private class Depleter extends Thread {
        private final static int DEPLETION_TIME = 2000;
        private boolean stopped = false;
        private boolean paused = false;

        public synchronized void finish() {
            stopped = true;
        }

        public synchronized void pause() {
            paused = true;
        }
        public synchronized void unpause() {
            paused = false;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(DEPLETION_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                synchronized (this) {
                    if (stopped)
                        break;

                    if (!paused) {
                        processWaterLvlChange(-1);
                        processFoodLvlChange(-1);
                    }
                }
            }
        }
    }

    final private Depleter depleter = new Depleter();

    final private int MAX_WATER_LVL;
    final private int MAX_FOOD_LVL;
    private int waterLvl;
    private final Object waterLvlGuard = new Object();
    private int foodLvl;
    private final Object foodLvlGuard = new Object();
    private State state = State.DEFAULT;
    private final Object stateGuard = new Object();

    private Place targetPlace = null;
    private boolean alive = true;

    private long lastReproduced;

    public Prey(Game game, String name, int health, int speed, int strength, String species, Position pos, int maxWaterLvl, int maxFoodLvl) {
        super(game, name, health, speed, strength, species, pos);
        this.MAX_FOOD_LVL = maxFoodLvl;
        this.MAX_WATER_LVL = maxWaterLvl;
        this.foodLvl = this.MAX_FOOD_LVL;
        this.waterLvl = this.MAX_WATER_LVL;
    }

    private void updateAliveStatus() {
        if (hasNoHealth()) {
            alive = false;
        }
    }

    @Deprecated
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

    @Deprecated
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

    public Place selectPlace(State desiredState) throws PreyPathInfeasibleException{
        Place bestPlace = switch (desiredState) {
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
        updateAliveStatus();
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
    }

    public synchronized boolean receiveAttack(Predator pred) {
        if (pos.equals(pred.getPos())) {
            if (strength < pred.strength) {
                decreaseHealth(pred.strength - strength);
            }
            System.out.println("Aw, damn, I got attacked! HP left: " + health);
            return true;
        }
        return false;
    }

    public void reproduce() {
        System.out.println("Reproducing!! (not implemented yet)");
        if (state == State.HIDING) {
            Prey child = new Prey(
                    game, name+"child", MAX_HEALTH, speed, strength, species, pos, MAX_WATER_LVL, MAX_FOOD_LVL
            );
            game.addAnimal(child, true);
        }
        else {
            throw new RuntimeException("Trying to reproduce not in hideout");
        }
    }

    public synchronized boolean hasNoHealth() {
        return (health <= 0);
    }

    @Override
    public synchronized void killSelf() {
        alive = false;
        depleter.finish();
        super.killSelf();
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

    private void leaveTargetPlace() {
        if (!pos.equals(targetPlace.getPos())) {
            throw new RuntimeException("Trying to leave Place, with positions of Place and Prey not equal");
        }
        targetPlace.leave(this);
        synchronized (stateGuard) {
            state = switch (state) {
                case EATING, DRINKING, HIDING -> State.DEFAULT;
                default -> throw new RuntimeException("Prey trying to exit Place from not a '...ING' state");
            };
        }
    }

    private void processChangeToState(State desiredState) {
        synchronized (stateGuard) {
            if (desiredState == State.MOVING_TO_FOOD || desiredState == State.MOVING_TO_WATER || desiredState == State.MOVING_TO_HIDEOUT) {
                if (state == State.EATING || state == State.DRINKING || state == State.HIDING) {
                    leaveTargetPlace();
                }
                try {
                    Place place = this.selectPlace(desiredState);
                    this.currPath = game.getMap().getPreyPath(this.pos, place.getPos());
                    this.targetPlace = place;
                    state = desiredState;
                } catch (PreyPathInfeasibleException e) {
                    throw new RuntimeException(e);
                }
            } else if (desiredState == State.DEFAULT) {
                if (state == State.EATING || state == State.DRINKING || state == State.HIDING) {
                    leaveTargetPlace();
                }
                state = desiredState;
            }
        }
    }

    private void processMoving() {
        System.out.println("About to make step on path");
        this.makeStepOnPath();
        if (this.currPath.isEmpty()) {
            this.currPath = null;
            depleter.pause();
            this.enterTargetPlace();
            depleter.unpause();
        }
    }

    @Override
    public void run() {
        depleter.setDaemon(true);
        depleter.start();

        lastReproduced = System.currentTimeMillis();

        while(true) {
            try {
                System.out.println("Current state: " + state);
                Thread.sleep(LOOP_TIME_DELAY / speed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (this) {
                if (!alive) {
                    break;
                }

                if (state == State.DEFAULT || state == State.HIDING || state == State.MOVING_TO_HIDEOUT) {
                    if (foodLvl <= Math.ceil(MAX_FOOD_LVL * 0.4)) {
                        processChangeToState(State.MOVING_TO_FOOD);
                    }
                    else if (waterLvl <= Math.ceil(MAX_WATER_LVL*0.4)) {
                        processChangeToState(State.MOVING_TO_WATER);
                    }

                    if (state == State.MOVING_TO_HIDEOUT) {
                        processMoving();
                    }
                    else if (state == State.HIDING) {
                        if (System.currentTimeMillis() > lastReproduced + 10000) {
                            lastReproduced = System.currentTimeMillis();
                            reproduce();
                            this.targetPlace.leave(this);
                            state = State.DEFAULT;
                        }
                    }
                    else if (state == State.DEFAULT) {
                        processChangeToState(State.MOVING_TO_HIDEOUT);
                    }
                }
                else if (state == State.MOVING_TO_FOOD || state == State.MOVING_TO_WATER) {
                    processMoving();
                }
                else if (state == State.EATING && foodLvl >= Math.floor(MAX_FOOD_LVL * 0.9)) {
                    processChangeToState(State.DEFAULT);
                }
                else if (state == State.DRINKING && waterLvl >= Math.floor(MAX_WATER_LVL * 0.9)) {
                    processChangeToState(State.DEFAULT);
                }
            }
        }
        killSelf();
    }

    public int getWaterLvl() {
        return waterLvl;
    }

    public int getMaxWaterLvl() {
        return MAX_WATER_LVL;
    }

    public int getFoodLvl() {
        return foodLvl;
    }

    public int getMaxFoodLvl() {
        return MAX_FOOD_LVL;
    }
}
