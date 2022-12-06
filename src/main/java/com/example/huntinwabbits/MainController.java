package com.example.huntinwabbits;

import game.mechanics.Map;
import game.mechanics.Position;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.List;
import java.util.ArrayList;

public class MainController {
    @FXML
    private Button updateButton;

    @FXML
    private AnchorPane mapPane;

    @FXML
    protected void onUpdateButtonClick() {
        int n = 10;

        Map map = null;
        try {
            map = new Map("src/main/resources/com/example/huntinwabbits/basicmap.txt");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        List<Cell> cells = new ArrayList<Cell>();
        for(int j=0;j<n;j++) {
            for(int i=0;i<n;i++) {
                Cell currCell = new Cell(10, map.getTileAsChar(new Position(j, i)));
                currCell.translateXProperty().bind(mapPane.widthProperty().multiply(1.0/n * i));
                currCell.translateYProperty().bind(mapPane.heightProperty().multiply(1.0/n * j));

                currCell.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
                currCell.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

                currCell.prefWidthProperty().bind(mapPane.widthProperty().multiply(1.0/n));
                currCell.prefHeightProperty().bind(mapPane.heightProperty().multiply(1.0/n));

                cells.add(currCell);
                mapPane.getChildren().add(currCell);
            }
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