package main;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

import controller.LoginScreen;
import controller.MainScreen;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import network.ChatManager;
import network.Communication;

import java.io.IOException;

/**
 * JavaFX application class for handling GUI
 */
public class FagiApp extends Application {
    private Stage primaryStage;
    private Scene scene;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if ( args.length != 0 )
            System.out.println("Usage: java LoginScreen");
        launch(args);
    }

    /**
     * Initial method called by the threat manager in JavFX.
     *
     * @param primaryStage Canvas for displaying scenes.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setOnCloseRequest(event -> {
            try {
                stop();
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        });

        scene = new Scene(new AnchorPane());

        ChatManager.setApplication(this);
        showLoginScreen();
        primaryStage.setTitle("Fagi Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Shows initial window for login. This method will also be called
     * when the user log out and the main screen shut down.
     */
    public void showLoginScreen() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginScreen.fxml"));
        LoginScreen controller = new LoginScreen();
        loader.setController(controller);
        try {
            scene.setRoot(loader.load());
        } catch (IOException e) {
            System.err.println(e.toString());
        }

        primaryStage.setResizable(false);
        controller.setPrimaryStage(primaryStage);
        controller.initCommunication();
        controller.initComponents();
        primaryStage.sizeToScene();
    }

    /**
     * Opens the main window with all user interface for chatting.
     * Switching controller to MainScreen.
     *
     * @param username Username logged in.
     * @param communication instance of Communication for the MainScreen.
     */
    public void showMainScreen(String username, Communication communication) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainScreen.fxml"));
        MainScreen controller = new MainScreen(username, communication);
        loader.setController(controller);
        try {
            scene.setRoot(loader.load());
        } catch (IOException e) {
            System.err.println(e.toString());
        }

        primaryStage.setResizable(true);
        controller.setPrimaryStage(primaryStage);
        controller.initComponents();
        controller.initCommunication();
        primaryStage.sizeToScene();
    }
}
