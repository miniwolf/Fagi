package com.fagi.main;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * JavaFX application class for handling GUI.
 */
public class FagiApp extends Application {
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

        ChatManager.setApplication(this);
        showLoginScreen();
        primaryStage.setTitle("Fagi Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Shows initial window for login. This method will also be called
     * when the user log out and the com.fagi.main screen shut down.
     */
    public void showLoginScreen() {
        // TODO : Let the user browse for the file path
        String configLocation = "config/serverinfo.config";
        new MasterLogin(this, configLocation, primaryStage, scene);
    }

    /**
     * Opens the com.fagi.main window with all user interface for chatting.
     * Switching com.fagi.controller to MainScreen.
     *
     * @param username      Username logged in.
     * @param communication instance of Communication for the MainScreen.
     */
    public void showMainScreen(String username, Communication communication) {
        MainScreen controller = new MainScreen(username, communication, primaryStage);
        FXMLLoader loader = new FXMLLoader(
                controller.getClass().getResource("/com/fagi/view/Main.fxml"));
        loader.setController(controller);
        try {
            scene.setRoot(loader.load());
        } catch (IOException e) {
            System.err.println(e.toString());
            return;
        }
        controller.initCommunication();
        primaryStage.sizeToScene();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        Platform.exit();
    }
}
