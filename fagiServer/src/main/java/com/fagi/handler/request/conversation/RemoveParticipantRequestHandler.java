package com.fagi.handler.request.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.model.conversation.RemoveParticipantRequest;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.worker.InputAgent;
import com.fagi.worker.OutputAgent;

public class RemoveParticipantRequestHandler implements RequestHandler<RemoveParticipantRequest> {
    private final Data data;
    private final InputAgent inputAgent;
    private final OutputAgent outputAgent;

    public RemoveParticipantRequestHandler(
            Data data,
            InputAgent inputAgent,
            OutputAgent outputAgent) {
        this.data = data;
        this.inputAgent = inputAgent;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<RemoveParticipantRequest> getRequestClass() {
        return RemoveParticipantRequest.class;
    }

    @Override
    public void handleRequest(RemoveParticipantRequest request) {
        outputAgent.addResponse(handleRemoveParticipant(request));
    }

    private Object handleRemoveParticipant(RemoveParticipantRequest request) {
        Conversation con = data.getConversation(request.id());
        if (con == null) {
            return new NoSuchConversation();
        }

        if (!con
                .getParticipants()
                .contains(inputAgent.getUsername())) {
            return new Unauthorized();
        }

        // TODO: Should we have permissions in conversations?

        User user = data.getUser(request.participant());
        if (user == null) {
            return new NoSuchUser();
        }

        // TODO: We should verify that the participant is in the conversation

        user.removeConversationID(request.id());
        con.removeUser(request.participant());
        data.storeConversation(con);
        data.storeUser(user);

        return new AllIsWell();
    }
}
