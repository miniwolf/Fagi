/*
 * COPYRIGHT © Nicklas 'MiNiWolF' Pingel and Jonas 'Jonne' Hartwig 2011
 * Login.java
 *
 * Handles login requests and respons to and from server.
 */

import javax.swing.*;
import java.util.regex.Pattern;

/**
 * TODO: Add descriptions
 */
class LoginManager {
    private final Communication communication;

    public LoginManager(Communication communication) {
        this.communication = communication;
    }

    /**
     * @param login       contains the login object.
     * @param username    string from the textField.getText method.
     * @param jCreateUser respond to the user is posted here.
     * @param loginScreen is used for disposing and creating a new Mainscreen.
     */
    public void handleLogin(Login login, String username, JLabel jCreateUser, LoginScreen loginScreen) {
        if (isEmpty(login.getUsername()) || isEmpty(login.getPassword())) {
            jCreateUser.setText("Fields cannot be empty"); return;
        }

        communication.sendObject(login);
        Object object = communication.handleObjects();
        if ( object instanceof AllIsWellException ) {
            new MainScreen(username, communication);
            loginScreen.dispose();
        } else if ( object instanceof NoSuchUserException ) jCreateUser.setText("User doesn't exist");
        else if (object instanceof UserOnlineException) jCreateUser.setText("You're already online");
        else if (object instanceof PasswordException) jCreateUser.setText("Wrong password");
        else {
            jCreateUser.setText("Unknown Exception: " + object.toString());
            System.out.println("Unknown Exception: " + object.toString());
        }
    }

    /**
     * @param username   username from the LoginScreen.
     * @param password   password from the LoginScreen.
     * @param passRepeat for checking repeated password is equal.
     * @param jMessage   JLabel for writing status messages to the user.
     */
    public void handleCreateUser(String username, String password, String passRepeat, JLabel jMessage) {
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

        if ( object instanceof AllIsWellException ) jMessage.setText("User Created");
        else if ( object instanceof NoSuchUserException ) jMessage.setText("User already exists");
    }

    /**
     * @param friendRequest contains the FriendRequest object to be send using the communication class.
     */
    public void handleFriendRequest(FriendRequest friendRequest) {
        if ( isEmpty(friendRequest.getFriendUsername()) ) {
            JOptionPane.showMessageDialog(null, "Friend request cannot be empty", "Error in friend request.", JOptionPane.ERROR_MESSAGE);
            return;
        }

        communication.sendObject(friendRequest);
        Object object = communication.handleObjects();
        if ( object instanceof AllIsWellException )
            JOptionPane.showMessageDialog(null, "User has been added.", "FriendRequest Succeded", JOptionPane.PLAIN_MESSAGE);
        else if ( object instanceof Exception )
            JOptionPane.showMessageDialog(null, "Friend doesn't exists, try again.", "Error in friend request.", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * @param string which is escaped in all the illegal characters.
     * @return String the escaped string.
     */
    private String deleteIllegalCharacters(String string) {
        if ( string.contains("\"") ) string = string.replace("\"", "'");
        if ( string.contains(",") ) string = string.replace(",", ";");
        return string;
    }

    private boolean isEmpty(String string) {
        return string.equals("");
    }

    private boolean isValid(String string) {
        return Pattern.matches("\\w*", string);
    }
}