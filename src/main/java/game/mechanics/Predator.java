package game.mechanics;

class NoPreyException extends Exception {
    public NoPreyException() {
        super();
    }
    public NoPreyException(String str) {
        super(str);
    }
    public NoPreyException(String str, Exception cause) {
        super(str, cause);
    }
}

enum Mode {
    HUNTING, RELAXING
}

public class Predator extends Animal {
    private Mode mode = Mode.HUNTING;
    private Prey currTarget = null;
    private final static int RELAX_TIME = 5000;
    private final static int AFTER_ATTACK_PAUSE = 2000;

    public Predator(Game game, String name, int health, int speed, int strength, String species, Position pos) {
        super(game, name, health, speed, strength, species, pos);
    }

    public Prey selectPrey() throws NoPreyException {
        Prey best_prey = null;
        int best_dist = -1;
        for(Animal anim : this.game.getAnimals()) {
            if (anim instanceof Prey) {
                int dist = Position.distance(this.getPos(), anim.getPos());
                if (best_prey == null || dist < best_dist) {
                    best_prey = (Prey) anim;
                    best_dist = dist;
                }
            }
        }

        if (best_prey == null)
            throw new NoPreyException();

        return best_prey;
    }

    public boolean attack(Prey prey) {
        return prey.receiveAttack(this);
    }

    public void removeTarget(Prey prey) {
        if (this.currTarget == prey)
            this.currTarget = null;
    }

    @Override
    public void run() {
        while(true) {
            try {
                Thread.sleep(Animal.TIME_DELTA / speed);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(this.mode == Mode.HUNTING) {
                if(this.currTarget == null) {
                    try {
                        this.currTarget = this.selectPrey();
                    }
                    catch (NoPreyException ex) {
                        this.mode = Mode.RELAXING;
                        continue;
                    }

                }
                this.currPath = game.getMap().getPredatorPath(this.getPos(), this.currTarget.getPos());
                if (!currPath.isEmpty()) {
                    this.makeStepOnPath();
                }

                if (pos.equals(this.currTarget.getPos())) {
                    if (this.attack(this.currTarget)) {
                        this.mode = Mode.RELAXING;
                        System.out.println("Yo we started relaxin'");
                    }
                    else {
                        try {
                            Thread.sleep(Predator.AFTER_ATTACK_PAUSE);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            else if(this.mode == Mode.RELAXING) {
                try {
                    Thread.sleep(Predator.RELAX_TIME);
                    this.currTarget = this.selectPrey();
                    this.mode = Mode.HUNTING;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (NoPreyException e) {
                    System.out.println("Yo, we continue relaxin'");
                }

            }

        }
    }
}
