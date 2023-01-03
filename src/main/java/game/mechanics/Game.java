package game.mechanics;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

public class Game {
    private final Map map;
    private volatile List<Animal> animals;
    private final Object animalsGuard = new Object();
    private volatile List<Place> places;

//    private final Depleter depleter = new Depleter();

    public Game(String pathToMap) throws FileNotFoundException, InvalidMapException {
        this.map = new Map(pathToMap);
        this.animals = new ArrayList<Animal>();
        this.places = this.map.createPlaceList();
    }

    public final void begin() {
        for(Place place : this.getPlaces()) {
            if (place instanceof Source src) {
                Thread th = new Thread(src);
                th.setDaemon(true);
                th.start();
            }
        }

        for(Animal anim : this.getAnimals()) {
            Thread th = new Thread(anim);
            th.setDaemon(true);
            th.start();
        }
    }

    public final void pause() {
        System.out.println("Not implemented LOL");
    }
    public final void resume() {
        System.out.println("Not implemented LOL");
    }

    public void addAnimal(Animal anim, boolean autostart) {
        synchronized (animalsGuard) {
            this.animals.add(anim);
        }
        if (autostart) {
            Thread th = new Thread(anim);
            th.setDaemon(true);
            th.start();
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
