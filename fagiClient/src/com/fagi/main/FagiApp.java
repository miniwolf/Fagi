package com.fagi.main;
        /*
         * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig.
         */

import com.fagi.config.ServerConfig;
import com.fagi.controller.MainScreen;
import com.fagi.controller.login.MasterLogin;
import com.fagi.controller.utility.Draggable;
import com.fagi.encryption.AES;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.util.DependencyInjectionSystem;
import com.fagi.utility.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

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
        if (args.length != 0) {
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
        ChatManager.setApplication(this);

        scene = new Scene(new AnchorPane());
        primaryStage.setScene(scene);

        showLoginScreen();
        primaryStage.setTitle("Fagi Welcome");
        primaryStage.show();
    }

    public void startCommunication(MasterLogin masterLogin) {
        // TODO: Let the user browse for the file path
        Thread thread = new Thread(() -> {
            AtomicBoolean successfulConnection = new AtomicBoolean(false);
            AES aes = new AES();
            aes.generateKey(128);
            Communication communication = DependencyInjectionSystem.getInstance().getInstance(
                    Communication.class);
            while (!successfulConnection.get()) {
                Platform.runLater(() -> {
                    try {
                        communication.connect(aes);
                        ChatManager.setCommunication(communication);
                        masterLogin
                                .setMessageLabel("Connected to server: " + communication.getName());
                        successfulConnection.set(true);
                    } catch (IOException e) {
                        Platform.runLater(
                                () -> masterLogin.setMessageLabel("Connection refused"));
                        e.printStackTrace();
                        Logger.logStackTrace(e);
                    }
                });

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Shows initial window for login. This method will also be called
     * when the user log out and the com.fagi.main screen shut down.
     */
    public MasterLogin showLoginScreen() {
        MasterLogin masterLogin = new MasterLogin(this, primaryStage, new Draggable(primaryStage));
        startCommunication(masterLogin);
        masterLogin.showMasterLoginScreen();
        return masterLogin;
    }

    /**
     * Opens the com.fagi.main window with all user interface for chatting.
     * Switching com.fagi.controller to MainScreen.
     *
     * @param username      Username logged in.
     */
    public void showMainScreen(String username) {
        MainScreen controller = new MainScreen(username, primaryStage);
        scene.setRoot(controller);
        controller.initCommunication();
        primaryStage.sizeToScene();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void stop() {
        Platform.exit();
        // TODO : Find better solution plz. There is at least two threads that is not interrupted when we either logout or close the login screen.
        System.exit(0);
    }
}
