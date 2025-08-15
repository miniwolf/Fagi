package com.fagi.handler.request.auth;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.Login;
import com.fagi.model.UserLoggedIn;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.Response;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

import java.util.List;

public class LoginRequestHandler implements RequestHandler<Login> {
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;
    private final Data data;

    public LoginRequestHandler(
            InputAgent inputAgent,
            OutputAgent outputAgent,
            Data data) {
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
        this.data = data;
    }

    @Override
    public Class<Login> getRequestClass() {
        return Login.class;
    }

    @Override
    public void handleRequest(Login request) {
        outputAgent.addResponse(handleLogin(request));
    }

    private Response handleLogin(Login request) {
        Response response = data.userLogin(
                request.username(),
                request.password(),
                outputAgent,
                inputAgent
        );

        if (!(response instanceof AllIsWell)) {
            return response;
        }

        outputAgent.setUserName(request.username());
        inputAgent.setUsername(request.username());
        List<String> friends = data
                .getUser(inputAgent.getUsername())
                .getFriends();
        for (String user : friends) {
            if (data.isUserOnline(user)) {
                OutputAgent outputAgent = data.getOutputAgent(user);
                outputAgent.addMessage(new UserLoggedIn(inputAgent.getUsername()));
            }
        }

        return response;
    }
}
