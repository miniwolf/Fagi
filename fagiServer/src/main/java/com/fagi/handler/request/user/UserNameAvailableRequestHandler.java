package com.fagi.handler.request.user;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.UserNameAvailableRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.UserExists;
import com.fagi.worker.OutputAgent;

public class UserNameAvailableRequestHandler implements RequestHandler<UserNameAvailableRequest> {
    private final Data data;
    private final OutputAgent outputAgent;

    public UserNameAvailableRequestHandler(
            Data data,
            OutputAgent outputAgent) {
        this.data = data;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<UserNameAvailableRequest> getRequestClass() {
        return UserNameAvailableRequest.class;
    }

    @Override
    public void handleRequest(UserNameAvailableRequest request) {
        outputAgent.addResponse(handleUserNameAvailableRequest(request));
    }

    private Object handleUserNameAvailableRequest(UserNameAvailableRequest request) {
        if (data.getUser(request.username()) == null) {
            return new AllIsWell();
        } else {
            return new UserExists();
        }
    }
}
