package com.example.demo;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view1.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 700, 700);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        stage.setTitle("RPG");
        HelloController controller = fxmlLoader.getController();
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch(event.getCode()){
                    case W:
                        controller.moveUp();
                        break;
                    case D:
                        controller.moveRight();
                        break;
                    case S:
                        controller.moveDown();
                        break;
                    case A:
                        controller.moveLeft();
                        break;
                    case SPACE:
                        controller.handleSpaceBar();
//                    case ENTER:
//                        controller.handleEnter();
                }
            }
        });
        stage.setScene(scene);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}