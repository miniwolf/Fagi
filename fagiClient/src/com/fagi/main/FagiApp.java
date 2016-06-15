package com.fagi.main;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
 */

import com.fagi.controller.LoginScreen;
import com.fagi.controller.MainScreen;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.MalformedURLException;

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
        try {

            LoginScreen controller = new LoginScreen(this);
            FXMLLoader loader =
                    new FXMLLoader(controller.getClass()
                                             .getResource("/com/fagi/view/LoginScreen.fxml"));
            loader.setController(controller);

            scene.setRoot(loader.load());
            scene.getStylesheets().add(MainScreen.class
                                                 .getResource("/com/fagi/style/LoginScreen.css")
                                                 .toExternalForm());
            this.primaryStage.setOnCloseRequest(event -> {
                try {
                    ChatManager.closeCommunication();
                    stop();
                } catch (Exception e) {
                    System.err.println(e.toString());
                }
            });
            primaryStage.setResizable(false);
            controller.setPrimaryStage(primaryStage);
            controller.initCommunication();
            controller.initComponents();
            primaryStage.sizeToScene();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println(e.toString());
        }
    }

    /**
     * Opens the com.fagi.main window with all user interface for chatting.
     * Switching com.fagi.controller to MainScreen.
     *
     * @param username      Username logged in.
     * @param communication instance of Communication for the MainScreen.
     */
    public void showMainScreen(String username, Communication communication) {
        MainScreen controller = new MainScreen(username, communication);
        FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/com/fagi/view/MainScreen.fxml"));
        loader.setController(controller);
        try {
            scene.setRoot(loader.load());
        } catch (IOException e) {
            System.err.println(e.toString());
            return;
        }

        this.primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.setIconified(true);
        });

        primaryStage.setResizable(true);
        controller.setPrimaryStage(primaryStage);
        controller.initCommunication();
        controller.initComponents();
        primaryStage.sizeToScene();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}
