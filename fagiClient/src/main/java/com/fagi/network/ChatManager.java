package com.fagi.network;
/*
 * Copyright (c) 2014. Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig
 * ChatManager.java
 */

import com.fagi.main.FagiApp;
import com.fagi.model.CreateUser;
import com.fagi.model.Logout;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.IllegalInviteCode;
import com.fagi.responses.Response;
import com.fagi.responses.UserExists;
import javafx.scene.control.Label;

import java.util.ServiceLoader;
import java.util.regex.Pattern;

/**
 * Chatmanager is used to handle all communication about the chat.
 * Sending Messages, handling friend requests and so on.
 * Handles login requests and responds to and from server.
 */
public class ChatManager {
    private static Communication communication = null;
    private static FagiApp application;
    private ServiceLoader<Communication> communicationLoader;

    public void test() {
        communicationLoader = ServiceLoader.load(Communication.class);
        communicationLoader
                .iterator()
                .next();
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
            System.err.println("Could not log out properly. " + "Shut down and let server handle the response");
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
    public static boolean handleCreateUser(
            String username,
            String password,
            Label labelMessage,
            String inviteCode) {
        if (isEmpty(username) || isEmpty(password) || isEmpty(inviteCode)) {
            labelMessage.setText("Fields can't be empty");
            return false;
        }

        if (!isValidUserName(username)) {
            System.out.println(username);
            labelMessage.setText("Username may not contain special symbols");
            return false;
        }

        CreateUser createUser = new CreateUser(username, password, toInteger(inviteCode));
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
        return Integer.parseInt(inviteCode);
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