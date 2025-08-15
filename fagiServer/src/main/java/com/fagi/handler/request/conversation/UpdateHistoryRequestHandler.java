package com.fagi.handler.request.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.HistoryUpdates;
import com.fagi.model.User;
import com.fagi.model.conversation.UpdateHistoryRequest;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.worker.OutputAgent;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class UpdateHistoryRequestHandler implements RequestHandler<UpdateHistoryRequest> {
    private final Data data;
    private final OutputAgent outputAgent;

    public UpdateHistoryRequestHandler(
            Data data,
            OutputAgent outputAgent) {
        this.data = data;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<UpdateHistoryRequest> getRequestClass() {
        return UpdateHistoryRequest.class;
    }

    @Override
    public void handleRequest(UpdateHistoryRequest request) {
        Object response = handleUpdateHistory(request);

        if ((response instanceof HistoryUpdates)) {
            outputAgent.addResponse(new AllIsWell());
        }
        outputAgent.addResponse(response);
    }

    private Object handleUpdateHistory(UpdateHistoryRequest request) {
        User user = data.getUser(request.sender());

        if (Objects.isNull(user)) {
            return new NoSuchUser();
        }

        if (!user
                .getConversationIDs()
                .contains(request.conversationID())) {
            return new Unauthorized();
        }

        Conversation con = data.getConversation(request.conversationID());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<TextMessage> res = con.getMessagesFromDate(new Timestamp(request
                                                                              .dateLastMessageReceived()
                                                                              .getTime()));

        return new HistoryUpdates(
                res,
                request.conversationID()
        );
    }
}
