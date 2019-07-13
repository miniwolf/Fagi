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
import com.fagi.threads.ThreadPool;
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
    private ThreadPool threadPool;

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
        this.threadPool = new ThreadPool();
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);
        ChatManager.setApplication(this);

        scene = new Scene(new AnchorPane());
        primaryStage.setScene(scene);

        showLoginScreen();
        primaryStage.setTitle("Fagi Welcome");
        primaryStage.show();
    }

    private void startCommunication(final MasterLogin masterLogin,
                                    final Communication communication) {
        // TODO: Let the user browse for the file path
        Runnable runnable = () -> {
            AtomicBoolean successfulConnection = new AtomicBoolean(false);
            AES aes = new AES();
            aes.generateKey(128);

            while (!successfulConnection.get()) {
                Platform.runLater(() -> {
                    try {
                        communication.connect(aes, threadPool);
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
        };
        threadPool.startThread(runnable, "Connection thread");
    }

    /**
     * Shows initial window for login. This method will also be called
     * when the user log out and the com.fagi.main screen shut down.
     */
    public MasterLogin showLoginScreen() {
        Communication communication = setupCommunication();
        if (communication == null) {
            return null;
        }

        MasterLogin masterLogin = new MasterLogin(this, communication, primaryStage, new Draggable(primaryStage));

        startCommunication(masterLogin, communication);
        masterLogin.showMasterLoginScreen();
        return masterLogin;
    }

    private Communication setupCommunication() {
        ServerConfig config;
        try {
            String configLocation = "config/serverinfo.config";
            config = ServerConfig.pathToServerConfig(configLocation);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return new Communication(config.getName(), config.getIp(), config.getPort(),
                config.getServerKey());
    }

    /**
     * Opens the com.fagi.main window with all user interface for chatting.
     * Switching com.fagi.controller to MainScreen.
     *
     * @param username      Username logged in.
     */
    public void showMainScreen(String username, Communication communication) {
        MainScreen controller = new MainScreen(username, communication, primaryStage);
        scene.setRoot(controller);
        controller.initCommunication(threadPool);
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
