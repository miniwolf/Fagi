package com.fagi.login;

import com.fagi.model.UserNameAvailableRequest;
import com.fagi.network.ChatManager;
import com.fagi.network.Communication;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.Response;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import java.util.regex.Pattern;

public class CheckUsername {
    private String username;
    private Communication communication;

    public CheckUsername(String username, Communication communication) {
        this.username = username;
        this.communication = communication;
    }

    private static boolean isValidUserName(String string) {
        return Pattern.matches("\\w*", string);
    }

    private static boolean checkIfUserNameIsAvailable(String username, Communication communication) {
        UserNameAvailableRequest request = new UserNameAvailableRequest(username);

        communication.sendObject(request);
        Response response = communication.getNextResponse();

        return response instanceof AllIsWell;
    }

    public CheckUsernameResult checkValidUsername() {
        System.out.println("Checking for validity");
        if (username == null || username.trim().equals("")) {
            return CheckUsernameResult.Empty;
        }
        boolean valid = isValidUserName(username);

        if (!valid) {
            return CheckUsernameResult.InvalidCharacters;
        }

        boolean available = checkIfUserNameIsAvailable(username, communication);

        if (!available) {
            return CheckUsernameResult.AlreadyInUse;
        }
        return CheckUsernameResult.Valid;
    }
}
