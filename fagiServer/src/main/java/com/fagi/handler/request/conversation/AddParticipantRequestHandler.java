package com.fagi.handler.request.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.AddParticipantRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.responses.UserExists;
import com.fagi.worker.OutputAgent;

import java.util.List;

public class AddParticipantRequestHandler implements RequestHandler<AddParticipantRequest> {
    private final Data data;
    private final OutputAgent outputAgent;

    public AddParticipantRequestHandler(
            Data data,
            OutputAgent outputAgent) {
        this.data = data;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<AddParticipantRequest> getRequestClass() {
        return AddParticipantRequest.class;
    }

    @Override
    public void handleRequest(AddParticipantRequest request) {
        outputAgent.addResponse(handleAddParticipant(request));
    }

    private Object handleAddParticipant(AddParticipantRequest request) {
        Conversation con = data.getConversation(request.id());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<String> conversationPariticipants = con.getParticipants();
        if (!conversationPariticipants.contains(request.sender())) {
            return new Unauthorized();
        }

        if (conversationPariticipants.contains(request.participant())) {
            return new UserExists();
        }

        User user = data.getUser(request.participant());
        if (user == null) {
            return new NoSuchUser();
        }

        con.addUser(user.getUserName());
        user.addConversationID(con.getId());

        if (data.isUserOnline(user.getUserName())) {
            OutputAgent outputAgent = data.getOutputAgent(user.getUserName());
            outputAgent.addResponse(con);
        }

        data.storeConversation(con);
        data.storeUser(user);

        return new AllIsWell();
    }
}
