package com.fagi.handler.request.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.conversation.ConversationDataUpdate;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.conversation.GetConversationsRequest;
import com.fagi.worker.OutputAgent;

import java.sql.Timestamp;
import java.util.List;

public class GetConversationsRequestHandler implements RequestHandler<GetConversationsRequest> {
    private final Data data;
    private final OutputAgent outputAgent;

    public GetConversationsRequestHandler(
            Data data,
            OutputAgent outputAgent) {
        this.data = data;
        this.outputAgent = outputAgent;
    }

    @Override
    public Class<GetConversationsRequest> getRequestClass() {
        return GetConversationsRequest.class;
    }

    @Override
    public void handleRequest(GetConversationsRequest request) {
        // Find all conversations user is participating in
        List<Long> conversationIDs = data
                .getUser(request.userName())
                .getConversationIDs();

        // Find conversations not in request and return them as placeholders
        conversationIDs
                .stream()
                .filter(x -> request
                        .filters()
                        .stream()
                        .noneMatch(y -> y.id() == x))
                .forEach(x -> outputAgent.addResponse(data
                                                              .getConversation(x)
                                                              .getPlaceholder()));

        // Find conversations from the request
        // For each conversation we get the messages since the last received message in request
        // For each conversation we find the last message and when it was sent
        request
                .filters()
                .stream()
                .filter(x -> conversationIDs.contains(x.id()))
                .forEach(x -> {
                    Conversation conversation = data.getConversation(x.id());
                    Timestamp time = new Timestamp(x
                                                           .lastMessageDate()
                                                           .getTime());
                    Timestamp lastMessageReceived = new Timestamp(conversation
                                                                          .getLastMessageDate()
                                                                          .getTime());
                    ConversationDataUpdate res = new ConversationDataUpdate(
                            x.id(),
                            conversation.getMessagesFromDate(time),
                            lastMessageReceived,
                            conversation.getLastMessage()
                    );

                    outputAgent.addResponse(res);
                });
    }
}
