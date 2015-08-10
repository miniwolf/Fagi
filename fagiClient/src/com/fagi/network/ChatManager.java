package com.fagi.network;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * ChatManager.java
 */

import com.fagi.controller.ErrorBoxController;
import com.fagi.exceptions.AllIsWellException;
import com.fagi.exceptions.NoSuchUserException;
import com.fagi.exceptions.PasswordException;
import com.fagi.exceptions.UserExistsException;
import com.fagi.exceptions.UserOnlineException;
import com.fagi.model.*;

import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.FagiApp;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * Chatmanager is used to handle all communication about the chat.
 * Sending messages, handling friend requests and so on.
 * Handles login requests and responds to and from server.
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
     * @param login           contains the login object.
     * @param username        string from the textField.getText method.
     * @param labelCreateUser respond to the user is posted here.
     */
    public static void handleLogin(Login login, String username, Label labelCreateUser) {
        if ( isEmpty(login.getUsername()) || isEmpty(login.getPassword()) ) {
            labelCreateUser.setText("Fields cannot be empty");
            return;
        }

        communication.sendObject(login);
        Exception exception = null;
        while ( exception == null ) {
            exception = communication.getNextException();
        }
        if ( exception instanceof AllIsWellException ) {
            application.showMainScreen(username, communication);
        } else {
            labelCreateUser.setText(exception instanceof NoSuchUserException
                                    ? "User doesn't exist"
                                    : exception instanceof UserOnlineException
                                      ? "You're already online"
                                      : exception instanceof PasswordException
                                        ? "Wrong password"
                                        : "Unknown Exception: " + exception.toString());
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
        Exception exception = null;
        while ( exception == null ) {
            exception = communication.getNextException();
        }
        if ( !(exception instanceof AllIsWellException) ) {
            System.err.println("Could not log out properly. "
                               + "Shut down and let server handle the exception");
        }
        communication.close();
        application.showLoginScreen();
    }

    public static void closeCommunication() {
        communication.close();
    }

    /**
     * @param username     username from the LoginScreen.
     * @param password     password from the LoginScreen.
     * @param passRepeat   for checking repeated password is equal.
     * @param labelMessage JLabel for writing status messages to the user.
     */
    public static void handleCreateUser(String username, String password, String passRepeat,
                                        Label labelMessage) {
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

        Exception exception = null;
        while ( exception == null ) {
            exception = communication.getNextException();
        }

        if ( exception instanceof AllIsWellException ) {
            labelMessage.setText("User Created");
        } else if ( exception instanceof UserExistsException ) {
            labelMessage.setText("Error: User already exists");
        }
    }

    /**
     * @param friendRequest contains the FriendRequest object to be send
     *                      using the communication class.
     */
    public static void handleFriendRequest(FriendRequest friendRequest) {
        if ( isEmpty(friendRequest.getFriendUsername()) ) {
            System.err.println("Friend request cannot be empty");
            showErrorMessage("Error in friend request.", "Friend request cannot be empty");
            return;
        }
        communication.sendObject(friendRequest);

        Exception exception = null;
        while ( exception == null ) {
            exception = communication.getNextException();
        }
        if ( exception instanceof AllIsWellException ) {
            // TODO: Maybe use version of showErrorMessage on this too.
            JOptionPane.showMessageDialog(null, "User has been added.", "FriendRequest Succeeded",
                                          JOptionPane.PLAIN_MESSAGE);
        } else if ( exception instanceof UserExistsException ) {
            showErrorMessage("FriendRequest Failed", "Request has already been made.");
        } else if ( exception instanceof NoSuchUserException ) {
            showErrorMessage("Error in friend request.", "Friend doesn't exist, try again.");
        } else {
            System.out.println(exception.toString());
        }
    }

    /**
     * Used for deleting friend request.
     * Sends a delete friend request object to the server.
     *
     * @param friendRequest FriendRequest object that we want to delete from the server.
     */
    public static void handleRequestDelete(FriendRequest friendRequest) {
        communication.sendObject(new DeleteFriendRequest(friendRequest.getFriendUsername()));
        Exception exception = null;
        while ( exception == null ) {
            exception = communication.getNextException();
        }
        if ( exception instanceof AllIsWellException ) {
            JOptionPane.showMessageDialog(null, "Successfully deleted.", "Success",
                                          JOptionPane.PLAIN_MESSAGE);
        } else {
            showErrorMessage("Delete Friend Request", "Somethign went wrong");
            //JOptionPane.showMessageDialog(null, "Something went wrong.",
            // "Error in delete request.", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param friendName Name of the friend we want to remove.
     */
    public static void handleFriendDelete(String friendName) {
        if ( friendName.length() == 0 ) {
            return;
        }
        communication.sendObject(new DeleteFriend(friendName));

        Exception exception = null;
        while ( exception == null ) {
            exception = communication.getNextException();
        }
        if ( !(exception instanceof AllIsWellException) ) {
            System.out.println(exception.toString());
        }
    }

    /**
     * Pop up error message. Utility method for showing message to the user with
     * an OK button.
     * @param title     The title of the popup message
     * @param message   Message to write to the user
     */
    public static void showErrorMessage(String title, String message) {
        try {
            //URL f =
            // new File("D:/Github/Fagi/fagiClient/src/com.fagi.view/RequestRespond.fxml")
            // .toURI().toURL();
            FXMLLoader loader =
                    new FXMLLoader(ErrorBoxController.class
                                              .getResource("/com/fagi/view/ErrorBox.fxml"));
            Pane page = loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle(title);
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(application.getPrimaryStage());

            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            ErrorBoxController controller = loader.getController();
            controller.setStage(dialogStage);
            controller.setText(message);
            dialogStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param string which is escaped in all the illegal characters.
     * @return String the escaped string.
     */
    private static String deleteIllegalCharacters(String string) {
        string = string.replace("\"", "'");
        string = string.replace(",", ";");
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