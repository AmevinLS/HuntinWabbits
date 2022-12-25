package com.example.huntinwabbits;

import game.mechanics.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button updateButton;
    @FXML
    private Button pauseButton;
    @FXML
    private AnchorPane mapPane;
    @FXML
    private ComboBox<Animal> animalComboBox;
//    List<Cell> cells = null;
    @FXML
    private Label healthLabel;
    @FXML
    private Label waterLabel;
    @FXML
    private Label foodLabel;

    Canvas canvas;
    Game game = null;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        int n = 10;
        try {
            this.game = new Game("src/main/resources/com/example/huntinwabbits/basicmap.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Predator pred = new Predator(
                this.game, "Kek", 10, 3, 4, "dog",
                new Position(3, 1)
        );
        Prey prey = new Prey(
                this.game, "Lol", 10, 2, 2, "cat",
                new Position(5, 8), 10, 10
        );
        Prey prey2 = new Prey(
                this.game, "Lol", 10, 2, 2, "cat",
                new Position(8, 4), 13, 13
        );
        Predator pred2 = new Predator(
                this.game, "Kek", 10, 3, 4, "dog",
                new Position(6, 2)
        );

//        this.game.addAnimal(pred);
        this.game.addAnimal(prey);
//        this.game.addAnimal(prey2);
//        this.game.addAnimal(pred2);


        canvas = new Canvas();
        canvas.widthProperty().bind(mapPane.widthProperty());
        canvas.heightProperty().bind(mapPane.heightProperty());
        this.mapPane.getChildren().add(canvas);

//        Map map = this.game.getMap();
//        cells = new ArrayList<Cell>();
//        for(int j=0;j<n;j++) {
//            for(int i=0;i<n;i++) {
//                Cell currCell = new Cell(10, map.getTileAsChar(new Position(j, i)));
//                currCell.translateXProperty().bind(mapPane.widthProperty().multiply(1.0/n * i));
//                currCell.translateYProperty().bind(mapPane.heightProperty().multiply(1.0/n * j));
//
//                currCell.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
//                currCell.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
//
//                currCell.prefWidthProperty().bind(mapPane.widthProperty().multiply(1.0/n));
//                currCell.prefHeightProperty().bind(mapPane.heightProperty().multiply(1.0/n));
//
//                cells.add(currCell);
//                mapPane.getChildren().add(currCell);
//            }
//        }
    }

    @FXML
    protected void onCanvasMouseClick(MouseEvent event) {
        double mouseX = event.getX(), mouseY = event.getY();
        double tileWidth = getTileWidth(), tileHeight = getTileHeight();

        int xInd = (int)(mouseY / tileWidth), yInd = (int)(mouseX / tileHeight);
        Position pos = new Position(xInd, yInd);

        animalComboBox.getItems().clear();
        for(Animal animal : game.getAnimals()) {
            if(animal.getPos().equals(pos)) {
                animalComboBox.getItems().add(animal);
            }
        }
        if(animalComboBox.getItems().size() > 0) {
            animalComboBox.getSelectionModel().select(0);
        }
    }

    @FXML
    protected void onUpdateButtonClick() {
        this.game.begin();
    }

    @FXML
    protected void onPauseButtonClick() {
        this.game.pause();
    }

    private double getTileWidth() {
        int nCols = game.getMap().getNumCols();
        return canvas.getWidth() / nCols;
    }
    private double getTileHeight() {
        int nRows = game.getMap().getNumRows();
        return canvas.getHeight() / nRows;
    }
    private void drawPath(Path path, Position startPos) {
        var gc = canvas.getGraphicsContext2D();
        double tileWidth = getTileWidth(), tileHeight = getTileHeight();

        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(5);
        Position lastPos = startPos;
        for(int i=0; i<path.size(); i++) {
            Position pos = path.get(i);

            gc.strokeLine(
                    lastPos.getY()*tileWidth + tileWidth/2, lastPos.getX()*tileHeight + tileHeight/2,
                    pos.getY()*tileWidth + tileWidth/2, pos.getX()*tileHeight + tileHeight/2
            );

            lastPos = pos;
        }
    }

    public void refreshMap() {
        var gc = canvas.getGraphicsContext2D();

        double canvWidth = canvas.getWidth();
        double canvHeight = canvas.getHeight();

        Map map = game.getMap();
        double tileWidth = canvWidth / map.getNumCols();
        double tileHeight = canvHeight / map.getNumRows();

        for(int i=0; i<map.getNumRows(); i++) {
            for(int j=0; j<map.getNumCols(); j++) {
                Tile tile = map.getTile(i, j);
                if (tile instanceof Road) {
                    gc.setFill(Color.BROWN);
                    gc.setStroke(Color.BLACK);
                }
                else if (tile instanceof Grass) {
                    gc.setFill(Color.GREEN);
                    gc.setStroke(Color.DARKGREEN);
                }
                else if (tile instanceof Intersection) {
                    gc.setFill(Color.SADDLEBROWN);
                    gc.setStroke(Color.BLACK);
                }
                else if (tile instanceof FoodSource) {
                    gc.setFill(Color.DARKORANGE);
                    gc.setStroke(Color.ORANGERED);
                }
                else if (tile instanceof WaterSource) {
                    gc.setFill(Color.CYAN);
                    gc.setStroke(Color.BLUE);
                }
                else if (tile instanceof Hideout) {
                    gc.setFill(Color.DARKGREY);
                }
                gc.fillRect(j*tileWidth, i*tileHeight, tileWidth, tileHeight);
                gc.strokeRect(j*tileWidth, i*tileHeight, tileWidth, tileHeight);
            }
        }

        for(Animal anim : game.getAnimals()) {
            if (anim instanceof Predator) {
                gc.setFill(Color.RED);
            }
            else if (anim instanceof Prey) {
                gc.setFill(Color.BLUE);
            }
            int x = anim.getPos().getX(), y = anim.getPos().getY();
            gc.fillOval(y*tileWidth + tileWidth/4, x*tileHeight + tileHeight/4, tileWidth/2, tileHeight/2);
        }

        Animal selAnimal = animalComboBox.getValue();
        String healthTxt = "None", waterTxt = "None", foodTxt = "None";
        if (selAnimal != null) {
            if (selAnimal.getCurrPath() != null) {
                drawPath(selAnimal.getCurrPath(), selAnimal.getPos());
            }

            healthTxt = "" + selAnimal.getHealth();

            if (selAnimal instanceof Prey selPrey) {
                waterTxt = "" + selPrey.getWaterLvl();
                foodTxt = "" + selPrey.getFoodLvl();
            }
        }
        healthLabel.setText("Health: " + healthTxt);
        waterLabel.setText("Water: " + waterTxt);
        foodLabel.setText("Food: " + foodTxt);
    }

    private class Cell extends StackPane {
        public Cell(double size, char type) {
            Rectangle border = new Rectangle(size, size);

            border.widthProperty().bind(this.widthProperty());
            border.heightProperty().bind(this.heightProperty());


            Color color = switch (type) {
                case 'F' -> Color.GREEN;
                case 'R' -> Color.BROWN;
                default -> null;
            };
            border.setFill(color);
            border.setOpacity(0.7);
            border.setStroke(color);


            this.setAlignment(Pos.CENTER);
            this.getChildren().addAll(border);
        }
    }
}