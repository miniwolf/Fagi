package com.fagi.handler.request.conversation;

import com.fagi.conversation.Conversation;
import com.fagi.handler.ConversationHandler;
import com.fagi.handler.request.RequestHandler;
import com.fagi.model.Data;
import com.fagi.model.messages.message.MessageInfo;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.responses.AllIsWell;
import com.fagi.responses.NoSuchConversation;
import com.fagi.responses.Unauthorized;
import com.fagi.worker.OutputAgent;

import java.sql.Timestamp;
import java.util.List;

/**
 * // TODO: zargess - javadoc
 */
public class TextMessageRequestHandler implements RequestHandler<TextMessage> {
    private final OutputAgent outputAgent;
    private final ConversationHandler conversationHandler;
    private final Data data;

    public TextMessageRequestHandler(
            OutputAgent outputAgent,
            ConversationHandler conversationHandler,
            Data data) {
        this.outputAgent = outputAgent;
        this.conversationHandler = conversationHandler;
        this.data = data;
    }

    @Override
    public Class<TextMessage> getRequestClass() {
        return TextMessage.class;
    }

    @Override
    public void handleRequest(TextMessage request) {
        MessageInfo messageInfo = request.getMessageInfo();
        messageInfo.setTimestamp(new Timestamp(System.currentTimeMillis()));
        outputAgent.addResponse(handleTextMessage(request));
    }

    private Object handleTextMessage(TextMessage textMessage) {
        MessageInfo messageInfo = textMessage.getMessageInfo();
        Conversation con = data.getConversation(messageInfo.getConversationID());
        if (con == null) {
            return new NoSuchConversation();
        }

        List<String> conversationParticipants = con.getParticipants();
        if (!conversationParticipants.contains(messageInfo.getSender())) {
            return new Unauthorized();
        }

        conversationHandler.addMessage(textMessage);

        return new AllIsWell();
    }
}
