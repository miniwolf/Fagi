package com.fagi.network;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * ChatManager.java
 */

import com.fagi.main.FagiApp;
import com.fagi.model.*;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.PasswordError;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import com.fagi.responses.UserOnline;

import javafx.scene.control.Label;

import java.util.regex.Pattern;

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
        if (isEmpty(login.getUsername()) || isEmpty(login.getPassword())) {
            labelCreateUser.setText("Fields cannot be empty");
            return;
        }

        communication.sendObject(login);
        Response response = communication.getNextResponse();
        if (response instanceof AllIsWell) {
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
        if (!(response instanceof AllIsWell)) {
            System.err.println("Could not log out properly. "
                               + "Shut down and let server handle the response");
        }
        communication.close();
        application.showLoginScreen();
    }

    /**
     * Will close the communication with the server.
     * This function is called from the quite action from @code{MasterLogin}
     */
    public static void closeCommunication() {
        if (communication == null) {
            return;
        }
        communication.close();
    }

    /**
     * @param username     username from the LoginScreen.
     * @param password     password from the LoginScreen.
     * @param labelMessage Label for writing status Messages to the user.
     * @param inviteCode   Invite code to verify that the user is admitted to the server
     */
    public static boolean handleCreateUser(String username, String password,
                                           Label labelMessage, String inviteCode) {
        if (isEmpty(username) || isEmpty(password) || isEmpty(inviteCode)) {
            labelMessage.setText("Fields can't be empty");
            return false;
        }

        if (!isValidUserName(username)) {
            System.out.println(username);
            labelMessage.setText("Username may not contain special symbols");
            return false;
        }

        CreateUser createUser = new CreateUser(username, password);
        createUser.setInviteCode(new InviteCode(toInteger(inviteCode)));
        communication.sendObject(createUser);

        Response response = communication.getNextResponse();
        if (response instanceof AllIsWell) {
            labelMessage.setText("User Created");
        } else if (response instanceof UserExists) {
            labelMessage.setText("Error: User already exists");
            return false;
        } else if (response instanceof IllegalInviteCode) {
            labelMessage.setText("Error: Illegal invite code. Contact host");
            return false;
        }
        return true;
    }

    private static int toInteger(String inviteCode) {
        return Integer.valueOf(inviteCode);
    }

    /**
     * Checks if a given username is available.
     *
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

    public static FagiApp getApplication() {
        return application;
    }

    private static boolean isEmpty(String string) {
        return string == null || "".equals(string);
    }
}