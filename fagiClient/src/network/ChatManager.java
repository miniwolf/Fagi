package network;/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * Login.java
 *
 * Handles login requests and responds to and from server.
 */

import exceptions.AllIsWellException;
import exceptions.NoSuchUserException;
import exceptions.PasswordException;
import exceptions.UserOnlineException;
import javafx.scene.control.Label;
import main.FagiApp;
import model.CreateUser;
import model.FriendRequest;
import model.Login;
import model.Logout;

import javax.swing.*;
import java.util.regex.Pattern;

/**
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
     * @param jCreateUser respond to the user is posted here.
     */
    public static void handleLogin(Login login, String username, Label jCreateUser) {
        if (isEmpty(login.getUsername()) || isEmpty(login.getPassword())) {
            jCreateUser.setText("Fields cannot be empty"); return;
        }

        communication.sendObject(login);
        Object object = communication.handleObjects();
        if ( object instanceof AllIsWellException) {
            application.showMainScreen(username, communication);
        } else if ( object instanceof NoSuchUserException) jCreateUser.setText("User doesn't exist");
        else if (object instanceof UserOnlineException) jCreateUser.setText("You're already online");
        else if (object instanceof PasswordException) jCreateUser.setText("Wrong password");
        else {
            jCreateUser.setText("Unknown Exception: " + object.toString());
            System.out.println("Unknown Exception: " + object.toString());
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
        while ( !(object instanceof AllIsWellException) )
            object = communication.handleObjects();
        communication.close();
        application.showLoginScreen();
    }

    public static void closeAllCommunication() {
        communication.close();
    }

    /**
     * @param username   username from the LoginScreen.
     * @param password   password from the LoginScreen.
     * @param passRepeat for checking repeated password is equal.
     * @param jMessage   JLabel for writing status messages to the user.
     */
    public static void handleCreateUser(String username, String password, String passRepeat, Label jMessage) {
        if ( isEmpty(username) || isEmpty(password) || isEmpty(passRepeat) ) {
            jMessage.setText("Fields can't be empty"); return;
        }

        if ( !password.equals(passRepeat) ) {
            jMessage.setText("Password's must match"); return;
        }

        if ( !isValid(username) ) {
            System.out.println(username);
            jMessage.setText("Username may not contain special symbols"); return;
        } else {
            password = deleteIllegalCharacters(password);
        }
        communication.sendObject(new CreateUser(username, password));
        Object object = communication.handleObjects();

        if ( object instanceof AllIsWellException) jMessage.setText("User Created");
        else if ( object instanceof NoSuchUserException) jMessage.setText("User already exists");
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
        if ( object instanceof AllIsWellException) {
            System.err.println("User has been added.");
            JOptionPane.showMessageDialog(null, "User has been added.", "FriendRequest Succeeded", JOptionPane.PLAIN_MESSAGE);
        } else if ( object instanceof Exception ) {
            System.err.println("Friend doesn't exist.");
            JOptionPane.showMessageDialog(null, "Friend doesn't exist, try again.", "Error in friend request.", JOptionPane.ERROR_MESSAGE);
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