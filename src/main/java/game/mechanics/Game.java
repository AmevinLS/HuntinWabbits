package game.mechanics;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

public class Game {
    private class Depleter extends Thread {
        private final static int DEPLETION_TIME = 1000;

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(DEPLETION_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (animalsGuard) {
                    for (Animal animal : animals) {
                        if (animal instanceof Prey) {
                            Prey prey = (Prey) animal;
                            prey.processWaterLvlDecrease(1);
                            prey.processFoodLvlDecrease(1);
                            System.out.println("Depleted stuff");
                        }
                    }
                }
            }
        }
    }

    private final Map map;
    private volatile List<Animal> animals;
    private final Object animalsGuard = new Object();
    private volatile List<Place> places;

    private final Depleter depleter = new Depleter();

    public Game(String pathToMap) throws FileNotFoundException, InvalidMapException {
        this.map = new Map(pathToMap);
        this.animals = new ArrayList<Animal>();
        this.places = this.map.createPlaceList();
    }

    public final void begin() {
        for(Animal anim : this.getAnimals()) {
            Thread th = new Thread(anim);
            th.setDaemon(true);
            th.start();
        }
        depleter.setDaemon(true);
        depleter.start();
    }

    public final void pause() {
//        try {
//            for (Animal anim : this.getAnimals()) {
//                anim.wait();
//            }
//            depleter.wait();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        System.out.println("Not implemented LOL");
    }
    public final void resume() {
//        for (Animal anim : this.getAnimals()) {
//            anim.notify();
//        }
//        depleter.notify();
        System.out.println("Not implemented LOL");
    }

    public void addAnimal(Animal anim) {
        synchronized (animalsGuard) {
            this.animals.add(anim);
        }
    }

    public void removeAnimal(Animal anim) {
        synchronized (animalsGuard) {
            animals.remove(anim);
            if (anim instanceof Prey) {
                for (Animal otherAnim : getAnimals()) {
                    if (otherAnim instanceof Predator) {
                        ((Predator) otherAnim).removeTarget((Prey)anim);
                    }
                }
            }
        }
    }

    public void addPlace(Place place) {
        this.places.add(place);
    }


    public Map getMap() {
        return this.map;
    }

    public List<Animal> getAnimals() {
        synchronized (animalsGuard){
            return this.animals;
        }
    }

    public List<Place> getPlaces() {
        return this.places;
    }
}
