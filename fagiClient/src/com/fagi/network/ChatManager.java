package com.fagi.network;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * ChatManager.java
 */

import com.fagi.controller.ErrorBoxController;
import com.fagi.exceptions.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.FagiApp;
import com.fagi.model.*;

import javax.swing.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.regex.Pattern;

/**
 * Chatmanager is used to handle all communication about the chat.
 * Sending messages, handling friend requests and so on.
 * Handles login requests and responds to and from server.
 * TODO: Add descriptions
 */
public class ChatManager {
    private static Communication communication = null;
    private static FagiApp application;

    /**
     * Sending a request to the server, asking for a login.
     * If the server cannot find a created user by the username it will return
     * a NoSuchUserException.
     * UserOnlineException will be returned if the user is already online, to avoid
     * multiple instances of the same user.
     * Wrong password will give PasswordException. This is done to distinguish
     * between wrong username and wrong password. (Better user experience...)
     *
     * @param login       contains the login object.
     * @param username    string from the textField.getText method.
     * @param labelCreateUser respond to the user is posted here.
     */
    public static void handleLogin(Login login, String username, Label labelCreateUser) {
        if ( isEmpty(login.getUsername()) || isEmpty(login.getPassword()) ) {
            labelCreateUser.setText("Fields cannot be empty");
            return;
        }

        communication.sendObject(login);
        Object object = communication.handleObjects();
        if ( object instanceof AllIsWellException ) {
            application.showMainScreen(username, communication);
        } else {
            labelCreateUser.setText(object instanceof NoSuchUserException ? "User doesn't exist"
                    : object instanceof UserOnlineException ? "You're already online"
                    : object instanceof PasswordException ? "Wrong password"
                    : "Unknown Exception: " + object.toString());
        }

    }

    /**
     * Logging out user by sending server request using the {Logout} object.
     * Scene will switch to LoginScreen again.
     *
     * @param logout contains the logout object.
     */
    public static void handleLogout(Logout logout) {
        communication.sendObject(logout);
        Object object = null;
        while (!(object instanceof AllIsWellException)) {
            object = communication.handleObjects();
        }
        communication.close();
        application.showLoginScreen();
    }

    public static void closeCommunication() {
        communication.close();
    }

    /**
     * @param username   username from the LoginScreen.
     * @param password   password from the LoginScreen.
     * @param passRepeat for checking repeated password is equal.
     * @param labelMessage   JLabel for writing status messages to the user.
     */
    public static void handleCreateUser(String username, String password, String passRepeat, Label labelMessage) {
        if ( isEmpty(username) || isEmpty(password) || isEmpty(passRepeat) ) {
            labelMessage.setText("Fields can't be empty");
            return;
        }

        if ( !password.equals(passRepeat) ) {
            labelMessage.setText("Password's must match");
            return;
        }

        if ( !isValid(username) ) {
            System.out.println(username);
            labelMessage.setText("Username may not contain special symbols");
            return;
        } else {
            password = deleteIllegalCharacters(password);
        }
        communication.sendObject(new CreateUser(username, password));
        Object object = communication.handleObjects();

        if ( object instanceof AllIsWellException ) {
            labelMessage.setText("User Created");
        } else if ( object instanceof UserExistsException ) {
            labelMessage.setText("User already exists");
        }
    }

    /**
     * @param friendRequest contains the FriendRequest object to be send using the communication class.
     */
    public static void handleFriendRequest(FriendRequest friendRequest) {
        if ( isEmpty(friendRequest.getFriendUsername()) ) {
            System.err.println("Friend request cannot be empty");
            JOptionPane.showMessageDialog(null, "Friend request cannot be empty", "Error in friend request.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        communication.sendObject(friendRequest);
        Object object = communication.handleObjects();
        // TODO : Make this code work
        if ( object instanceof AllIsWellException ) {
            JOptionPane.showMessageDialog(null, "User has been added.", "FriendRequest Succeeded",
                    JOptionPane.PLAIN_MESSAGE);
        } else if ( object instanceof Exception ) {
            //showErrorMessage("Error in friends request", "Friend doesn't exist, try again");
            JOptionPane.showMessageDialog(null, "Friend doesn't exist, try again.", "Error in friend request.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Used for deleting friend request.
     * Sends a delete friend request object to the server.
     *
     * @param friendRequest FriendRequest object that we want to delete from the server.
     */
    public static void handleRequestDelete(FriendRequest friendRequest) {
        if ( isEmpty(friendRequest.getFriendUsername()) ) {
            System.err.println("Friend request cannot be empty");
            JOptionPane.showMessageDialog(null, "Friend request cannot be empty", "Error in friend request.",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        communication.sendObject(new DeleteFriendRequest(friendRequest.getFriendUsername()));
        Object object = communication.handleObjects();
        if ( object instanceof Exception ) {
            JOptionPane.showMessageDialog(null, "Something went wrong.", "Error in delete request.",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // TODO : Make this shit work
    public static void showErrorMessage(String title, String message) {
        try {
            ErrorBoxController controller = new ErrorBoxController();
            //URL f = new File("D:/Github/Fagi/fagiClient/src/com.fagi.view/RequestRespond.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(controller.getClass().getResource("/com/fagi/view/ErrorBox.fxml"));
            Pane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            controller.setStage(dialogStage);
            //com.fagi.controller.setText(message);
            dialogStage.showAndWait();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param string which is escaped in all the illegal characters.
     * @return String the escaped string.
     */
    private static String deleteIllegalCharacters(String string) {
        if ( string.contains("\"") ) string = string.replace("\"", "'");
        if ( string.contains(",") ) string = string.replace(",", ";");
        return string;
    }

    public static void setCommunication(Communication communication) {
        ChatManager.communication = communication;
    }

    public static void setApplication(FagiApp application) {
        ChatManager.application = application;
    }

    private static boolean isEmpty(String string) {
        return string.equals("");
    }

    private static boolean isValid(String string) {
        return Pattern.matches("\\w*", string);
    }
}