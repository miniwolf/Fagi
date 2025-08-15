package com.fagi.handler.request.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.CreateConversationRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchUser;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateConversationRequestHandler implements RequestHandler<CreateConversationRequest> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public CreateConversationRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<CreateConversationRequest> getRequestClass() {
        return CreateConversationRequest.class;
    }

    @Override
    public void handleRequest(CreateConversationRequest request) {
        Object response = handleCreateConversation(request);
        if (!(response instanceof NoSuchUser)) {
            outputAgent.addResponse(new AllIsWell());
        }
        outputAgent.addResponse(response);
    }

    private Object handleCreateConversation(CreateConversationRequest request) {
        List<User> users = new ArrayList<>();
        for (String username : request.participants()) {
            User u = data.getUser(username);
            if (u == null) {
                return new NoSuchUser();
            }

            users.add(u);
        }

        Conversation con = data.createConversation(request.participants());

        for (User user : users) {
            user.addConversationID(con.getId());
            data.storeUser(user);
            boolean notCurrentUser = !Objects.equals(
                    user.getUserName(),
                    inputAgent.getUsername()
            );
            if (notCurrentUser && data.isUserOnline(user.getUserName())) {
                OutputAgent outputAgent = data.getOutputAgent(user.getUserName());
                outputAgent.addResponse(con);
            }
        }

        data.storeConversation(con);

        return con;
    }
}
