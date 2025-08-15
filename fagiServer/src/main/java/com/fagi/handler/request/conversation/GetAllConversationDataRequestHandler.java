package com.fagi.handler.request.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.conversation.GetAllConversationDataRequest;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.User;
import com.fagi.responses.NoSuchUser;
import com.fagi.responses.Unauthorized;
import com.fagi.worker.OutputAgent;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GetAllConversationDataRequestHandler implements RequestHandler<GetAllConversationDataRequest> {
    private final Data data;
    private final OutputAgent outputAgent;

    public GetAllConversationDataRequestHandler(
            Data data,
            OutputAgent outputAgent) {
        this.data = data;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<GetAllConversationDataRequest> getRequestClass() {
        return GetAllConversationDataRequest.class;
    }

    @Override
    public void handleRequest(GetAllConversationDataRequest request) {
        outputAgent.addResponse(handleGetAllConversationDataRequest(request));
    }

    private Object handleGetAllConversationDataRequest(GetAllConversationDataRequest request) {
        User user = data.getUser(request.sender());

        if (Objects.isNull(user)) {
            return new NoSuchUser();
        }

        List<Long> conversationIDs = user.getConversationIDs();
        if (!conversationIDs.contains(request.id())) {
            return new Unauthorized();
        }
        Conversation conversation = data.getConversation(request.id());
        Date lastMessageDate = conversation.getLastMessageDate();
        Timestamp lastMessageReceived = new Timestamp(lastMessageDate.getTime());
        return new ConversationDataUpdate(
                request.id(),
                conversation.getMessages(),
                lastMessageReceived,
                conversation.getLastMessage()
        );
    }
}
