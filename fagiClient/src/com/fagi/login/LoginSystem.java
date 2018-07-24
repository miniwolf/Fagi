package com.fagi.login;

import com.fagi.model.Login;
import com.fagi.network.Communication;
import com.fagi.responses.Response;

public class LoginSystem {
    private Communication communication;

    public LoginSystem(Communication communication) {
        this.communication = communication;
    }

    private static boolean isEmpty(String string) {
        return string == null || "".equals(string);
    }

    /**
     * Sending a request to the server, asking for a login.
     * If the server cannot find a created user by the username it will return
     * a NoSuchUser.
     * UserOnlineException will be returned if the user is already online, to avoid
     * multiple instances of the same user.
     * Wrong password will give PasswordException. This is done to distinguish
     * between wrong username and wrong password. (Better user experience...)
     *
     * @param loginData contains the login object.
     */
    public Response login(Login loginData) {
        if (isEmpty(loginData.getUsername()) || isEmpty(loginData.getPassword())) {
            return new FieldEmpty();
        }

        communication.sendObject(loginData);
        return communication.getNextResponse();
    }
}
