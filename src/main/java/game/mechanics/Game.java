package game.mechanics;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.ArrayList;

public class Game {
    private final Map map;
    private volatile List<Animal> animals;
    private final Object animalsGuard = new Object();
    private volatile List<Place> places;

    public Game(String pathToMap) throws FileNotFoundException, InvalidMapException {
        this.map = new Map(pathToMap);
        this.animals = new ArrayList<Animal>();
        this.places = new ArrayList<Place>();
    }

    public void addAnimal(Animal anim) {
        synchronized (animalsGuard) {
            this.animals.add(anim);
        }
    }

    public void removeAnimal(Animal anim) {
        synchronized (animalsGuard) {
            animals.remove(anim);
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
