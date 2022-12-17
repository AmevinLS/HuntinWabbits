package com.example.huntinwabbits;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CounterApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CounterApplication.class.getResource("Main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 500);
        stage.setTitle("Counting Stuff!");
        stage.setScene(scene);
        stage.show();

        Thread renderer = new Renderer(fxmlLoader.getController());
        renderer.setDaemon(true);
        renderer.start();
    }

    public static void main(String[] args) {
        launch();
    }

    class Renderer extends Thread {
        private final static int REFRESH_TIME = 50;
        final MainController controller;
        public Renderer(MainController control) {
            this.controller = control;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    Thread.sleep(REFRESH_TIME);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        controller.refreshMap();
                    }
                });
            }
        }
    }
}