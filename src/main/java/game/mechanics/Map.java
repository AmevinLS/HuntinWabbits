package game.mechanics;

import java.util.*;
import java.io.File;
import java.io.FileNotFoundException;


public class Map {
    private final List<List<Tile>> tiles;
    private final int numRows;
    private final int numCols;

    public int getNumRows() {
        return this.numRows;
    }

    public int getNumCols() {
        return this.numCols;
    }

    public Map(String pathToMap) throws FileNotFoundException, InvalidMapException {
        String errorMessage = "Couldn't parse map at " + pathToMap;
        this.tiles = new ArrayList<List<Tile>>();

        File f = new File(pathToMap);
        Scanner scan = new Scanner(f);

        this.numRows = scan.nextInt();
        this.numCols = scan.nextInt();
        scan.nextLine();

        for(int row=0; row<numRows; row++) {
            this.tiles.add(new ArrayList<Tile>());
            String line = scan.nextLine();

            if (line.length() != this.numCols){
                throw new InvalidMapException(errorMessage);
            }

            for(int i=0; i<line.length(); i++) {
                char ch = line.charAt(i);
                Position pos = new Position(row, i);
                Tile tile = switch (ch) {
                    case 'G' -> new Grass(pos);
                    case 'R' -> new Road(pos);
                    case 'I' -> new Intersection(pos, "IntersectName");
                    case 'F' -> new FoodSource(pos, "FoodName", 1, 1); // TODO: make possibility for other capacities
                    case 'W' -> new WaterSource(pos, "WaterName", 1, 1);
                    case 'H' -> new Hideout(pos, "HideoutName", 1);
                    default -> null;
                };
                this.tiles.get(row).add(tile);
            }
        }

        for(Place place : this.createPlaceList()) {
            int capacity = scan.nextInt();
            place.setMaxCapacity(capacity);
        }
    }

    public List<Place> createPlaceList() {
        List<Place> res = new ArrayList<Place>();
        for(int i=0;i<numRows;i++) {
            for(int j=0;j<numCols;j++) {
                Tile tile = this.getTile(i, j);
                if (tile instanceof Place) {
                    res.add((Place) tile);
                }
            }
        }
        return res;
    }

    public boolean isValidPos(Position pos) {
        if (pos.getX() < 0 || pos.getY() < 0)
            return false;
        if (pos.getX() >= numRows || pos.getY() >= numCols)
            return false;
        return true;
    }

    private boolean dfsPreyPath(Position currPos, Position targetPos, Path currPath) {
        if (currPos.equals(targetPos)) {
            return true;
        }

        for(Position pos : this.getNeighborPositions(currPos)) {
            if (currPath.contains(pos))
                continue;
            Tile tile = this.getTile(pos);
            if (tile instanceof PreyVisitable) {
                currPath.append(pos);
                if (dfsPreyPath(pos, targetPos, currPath)) {
                    return true;
                }
                currPath.popLastPosition();
            }
        }
        return false;
    }

    public Path getPreyPath(Position p1, Position p2) throws PreyPathInfeasibleException{
        Tile tile1 = this.getTile(p1), tile2 = this.getTile(p2);
        if(!(tile1 instanceof PreyVisitable) || !(tile2 instanceof PreyVisitable)) {
            throw new PreyPathInfeasibleException("One of tiles at " + p1 + " or " + p2 + " are not PreyVisitable");
        }
        Path resPath = new Path();
        resPath.append(p1);
        if (!dfsPreyPath(p1, p2, resPath)) {
            throw new PreyPathInfeasibleException("Couldn't build path between " + p1 + " " + p2);
        }
        return resPath;
    }

    public Path getPredatorPath(Position p1, Position p2) {
        Path resPath = new Path();
        int x1 = p1.getX(), y1 = p1.getY();
        int x2 = p2.getX(), y2 = p2.getY();

        int stepX = Integer.signum(x2 - x1), stepY = Integer.signum(y2 - y1);
        int currX = x1, currY = y1;

        while (currX != x2) {
            currX += stepX;
            resPath.append(new Position(currX, currY));
        }

        while (currY != y2) {
            currY += stepY;
            resPath.append(new Position(currX, currY));
        }

        return resPath;
    }

    public List<Position> getNeighborPositions(Position p) {
        List<Position> neighbs = new LinkedList<>();
        Position pos;
        int[] ds = {-1, 1};
        for(int d : ds) {
            pos = p.shift(d, 0);
            if (this.isValidPos(pos))
                neighbs.add(pos);

            pos = p.shift(0, d);
            if (this.isValidPos(pos))
                neighbs.add(pos);
        }
        return neighbs;
    }

    public Tile getTile(int x, int y) {
        return tiles.get(x).get(y);
    }

    public Tile getTile(Position p) {
        return this.getTile(p.getX(), p.getY());
    }

    public char getTileAsChar(Position p) {
        Tile tile = this.getTile(p);
        if (tile instanceof Road) {
            return 'R';
        }
        else if (tile instanceof Grass) {
            return 'F';
        }
        return 'X';
    }
}
