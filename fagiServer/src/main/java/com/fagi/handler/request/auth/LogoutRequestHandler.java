package com.fagi.handler.request.auth;

import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.Logout;
import com.fagi.model.UserLoggedOut;
import com.fagi.responses.AllIsWell;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

import java.util.List;

/**
 * // TODO: zargess - javadoc
 */
public class LogoutRequestHandler implements RequestHandler<Logout> {
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;
    private final Data data;

    public LogoutRequestHandler(
            InputAgent inputAgent,
            OutputAgent outputAgent,
            Data data) {
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
        this.data = data;
    }

    @Override
    public Class<Logout> getRequestClass() {
        return Logout.class;
    }

    @Override
    public void handleRequest(Logout request) {
        data.userLogout(inputAgent.getUsername());
        inputAgent.stop();

        List<String> friends = data
                .getUser(inputAgent.getUsername())
                .getFriends();
        for (String user : friends) {
            if (data.isUserOnline(user)) {
                OutputAgent outputAgent = data.getOutputAgent(user);
                outputAgent.addMessage(new UserLoggedOut(inputAgent.getUsername()));
            }
        }

        outputAgent.addResponse(new AllIsWell());
        outputAgent.stop();
    }
}
