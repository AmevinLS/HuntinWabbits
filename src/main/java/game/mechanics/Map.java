package game.mechanics;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


class InvalidMapException extends Exception {
    public InvalidMapException() {
        super();
    }
    public InvalidMapException(String str) {
        super(str);
    }
    public InvalidMapException(String str, Exception cause) {
        super(str, cause);
    }
}

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
        this.tiles = new ArrayList<List<Tile>>();

        File f = new File(pathToMap);
        Scanner scan = new Scanner(f);
        int row = 0;
        Integer col = null;
        while (scan.hasNextLine()) {
            this.tiles.add(new ArrayList<Tile>());
            String line = scan.nextLine();

            if (col == null) {
                col = line.length();
            }
            else if (col != line.length()){
                throw new InvalidMapException("Couldn't parse map at " + pathToMap);
            }

            for(int i=0; i<line.length(); i++) {
                char ch = line.charAt(i);
                Position pos = new Position(row, i);
                Tile tile = switch (ch) {
                    case 'F' -> new Field(pos);
                    case 'R' -> new Road(pos);
                    default -> null;
                };
                this.tiles.get(row).add(tile);
            }
            row++;
        }

        this.numRows = row;
        this.numCols = col;
    }

    public List<Place> createPlaceList() {
        List<Place> res = new ArrayList<Place>();
        return res;
    }

    public boolean isValidPos(Position pos) {
        if (pos.getX() < 0 || pos.getY() < 0)
            return false;
        if (pos.getX() >= numRows || pos.getY() >= numCols)
            return false;
        return true;
    }

    public Path getPreyPath(Position p1, Position p2) {
        // TODO
        return null;
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
        else if (tile instanceof Field) {
            return 'F';
        }
        return 'X';
    }
}
