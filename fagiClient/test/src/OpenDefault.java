/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

import com.fagi.controller.MainScreen;
import com.fagi.network.Communication;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by miniwolf on 07-12-2016.
 */
public class OpenDefault extends Application {
    private Stage primaryStage;
    private Scene scene;

    /**
     * Main method, launches the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ( args.length != 0 ) {
            System.out.println("Usage: java LoginScreen");
        }
        launch(args);
    }

    /**
     * Initial method called by the threat manager in JavFX.
     *
     * @param primaryStage Canvas for displaying scenes.
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        scene = new Scene(new AnchorPane());
        showMainScreen();
        primaryStage.setTitle("Fagi Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainScreen() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/fagi/view/login/createPassword.fxml"));
        try {
            scene.setRoot(loader.load());
        } catch (IOException e) {
            System.err.println(e.toString());
            return;
        }
        primaryStage.sizeToScene();
    }
}
