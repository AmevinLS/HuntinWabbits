package game.mechanics;

public class WaterSource extends Source {
    WaterSource(Position p, String name, int capacity, int replenishSpeed) {
        super(p, name, capacity, replenishSpeed);
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(REPLENISH_TIME / replenishSpeed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            synchronized (occupantsGuard) {
                for (Prey occupant : occupants) {
                    occupant.processFoodLvlChange(1);
                }
            }
        }
    }
}
