package com.example.huntinwabbits;

import game.mechanics.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button updateButton;

    @FXML
    private AnchorPane mapPane;

    List<Cell> cells = null;
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
                new Position(5, 8), 15, 15
        );
        Predator pred2 = new Predator(
                this.game, "Kek", 10, 3, 4, "dog",
                new Position(6, 2)
        );

        this.game.addAnimal(pred);
        this.game.addAnimal(prey);
        this.game.addAnimal(pred2);

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
    protected void onUpdateButtonClick() {
        for(Animal anim : this.game.getAnimals()) {
            Thread th = new Thread(anim);
            th.setDaemon(true);
            th.start();
        }
    }

    public void refreshMap() {
        var gc = canvas.getGraphicsContext2D();

        double canvWidth = canvas.getWidth();
        double canvHeight = canvas.getHeight();

        Map map = game.getMap();
        double cellWidth = canvWidth / map.getNumCols();
        double cellHeight = canvHeight / map.getNumRows();

        for(int i=0; i<map.getNumRows(); i++) {
            for(int j=0; j<map.getNumCols(); j++) {
                Tile tile = map.getTile(i, j);
                if (tile instanceof Road) {
                    gc.setFill(Color.BROWN);
                }
                else if (tile instanceof Field) {
                    gc.setFill(Color.GREEN);
                }
                gc.fillRect(j*cellWidth, i*cellHeight, cellWidth, cellHeight);
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
            gc.fillOval(y*cellWidth, x*cellHeight, cellWidth, cellHeight);
        }
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