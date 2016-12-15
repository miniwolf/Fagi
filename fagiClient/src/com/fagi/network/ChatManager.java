package com.fagi.network;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * ChatManager.java
 */

import com.fagi.controller.ErrorBoxController;
import com.fagi.main.FagiApp;
import com.fagi.model.*;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import com.fagi.responses.UserOnline;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;

/**
 * Chatmanager is used to handle all communication about the chat.
 * Sending Messages, handling friend requests and so on.
 * Handles login requests and responds to and from server.
 */
public class ChatManager {
    private static Communication communication = null;
    private static FagiApp application;

    /**
     * Sending a request to the server, asking for a login.
     * If the server cannot find a created user by the username it will return
     * a NoSuchUser.
     * UserOnlineException will be returned if the user is already online, to avoid
     * multiple instances of the same user.
     * Wrong password will give PasswordException. This is done to distinguish
     * between wrong username and wrong password. (Better user experience...)
     *
     * @param login           contains the login object.
     * @param labelCreateUser respond to the user is posted here.
     */
    public static void handleLogin(Login login, Label labelCreateUser) {
        if ( isEmpty(login.getUsername()) || isEmpty(login.getPassword()) ) {
            labelCreateUser.setText("Fields cannot be empty");
            return;
        }

        communication.sendObject(login);
        Response response = communication.getNextResponse();
        if ( response instanceof AllIsWell ) {
            application.showMainScreen(login.getUsername(), communication);
        } else {
            labelCreateUser.setText(response instanceof NoSuchUser
                                    ? "User doesn't exist"
                                    : response instanceof UserOnline
                                      ? "You are already online"
                                      : response instanceof PasswordError
                                        ? "Wrong password"
                                        : "Unknown Exception: " + response.toString());
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
        Response response = communication.getNextResponse();
        if ( !(response instanceof AllIsWell) ) {
            System.err.println("Could not log out properly. "
                               + "Shut down and let server handle the response");
        }
        communication.close();
        application.showLoginScreen();
    }

    public static void closeCommunication() {
        if ( communication == null ) {
            return;
        }
        communication.close();
    }

    /**
     * @param username     username from the LoginScreen.
     * @param password     password from the LoginScreen.
     * @param passRepeat   for checking repeated password is equal.
     * @param labelMessage JLabel for writing status Messages to the user.
     */
    public static boolean handleCreateUser(String username, String password, String passRepeat,
                                        Label labelMessage) {
        if ( isEmpty(username) || isEmpty(password) || isEmpty(passRepeat) ) {
            labelMessage.setText("Fields can't be empty");
            return false;
        }

        if ( !password.equals(passRepeat) ) {
            labelMessage.setText("Password's must match");
            return false;
        }

        if ( !isValidUserName(username) ) {
            System.out.println(username);
            labelMessage.setText("Username may not contain special symbols");
            return false;
        }

        communication.sendObject(new CreateUser(username, password));

        Response response = communication.getNextResponse();
        if ( response instanceof AllIsWell ) {
            labelMessage.setText("User Created");
        } else if ( response instanceof UserExists ) {
            labelMessage.setText("Error: User already exists");
            return false;
        }
        return true;
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

        Response Response = communication.getNextResponse();
        if ( Response instanceof AllIsWell ) {
            // TODO: Maybe use version of showErrorMessage on this too.
            JOptionPane.showMessageDialog(null, "User has been added.", "FriendRequest Succeeded",
                                          JOptionPane.PLAIN_MESSAGE);
        } else if ( Response instanceof UserExists ) {
            showErrorMessage("FriendRequest Failed", "Request has already been made.");
        } else if ( Response instanceof NoSuchUser ) {
            showErrorMessage("Error in friend request.", "Friend doesn't exist, try again.");
        } else {
            System.out.println(Response.toString());
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
        Response response = communication.getNextResponse();
        if ( response instanceof AllIsWell ) {
            JOptionPane.showMessageDialog(null, "Successfully deleted.", "Success",
                                          JOptionPane.PLAIN_MESSAGE);
        } else {
            showErrorMessage("Delete Friend Request", "Something went wrong");
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

        Response response = communication.getNextResponse();
        if ( !(response instanceof AllIsWell) ) {
            System.out.println(response.toString());
        }
    }

    /**
     * Pop up error message. Utility method for showing message to the user with
     * an OK button.
     * @param title     The title of the popup message
     * @param message   MessageInfo to write to the user
     */
    public static void showErrorMessage(String title, String message) {
        try {
            FXMLLoader loader = new FXMLLoader(ErrorBoxController.class.getResource("/com/fagi/view/ErrorBox.fxml"));
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
     * Checks if a given username is available
     * @param username username from the CreateUserNameScreen
     * @return true if the username is available
     */
    public static boolean checkIfUserNameIsAvailable(String username) {
        UserNameAvailableRequest request = new UserNameAvailableRequest(username);

        communication.sendObject(request);
        Response response = communication.getNextResponse();

        System.out.println(response.getClass());

        return response instanceof AllIsWell;
    }

    public static void setCommunication(Communication communication) {
        ChatManager.communication = communication;
    }

    public static void setApplication(FagiApp application) {
        ChatManager.application = application;
    }

    public static boolean isValidUserName(String string) {
        return Pattern.matches("\\w*", string);
    }

    private static boolean isEmpty(String string) {
        return string == null || "".equals(string);
    }
}