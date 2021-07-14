package com.fagi.controller.contentList;

import com.fagi.action.Action;
import com.fagi.conversation.Conversation;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.util.DateTimeUtils;

import java.util.Date;

/**
 * @author miniwolf
 */
public class MessageItemController extends TimedContentItemController<Long> {
    private static final String fxmlResource = "/view/content/ConversationItem.fxml";
    private final long ID;

    public MessageItemController(
            String username,
            Conversation conversation,
            Action<Long> action,
            Date date) {
        super(username, date, conversation.getParticipants(), fxmlResource, action);
        this.ID = conversation.getId();
        getStyleClass().add("contact");

        if (conversation.getLastMessage() != null) {
            setLastMessage(conversation.getLastMessage());
        }
    }

    public void setLastMessage(TextMessage message) {
        String sender = message
                .getMessageInfo()
                .getSender();
        boolean isMyMessage = sender.equals(username);
        String senderString = (isMyMessage ? "You" : sender);
        lastMessage.setText(senderString + ": " + cropMessage(message.data(), isMyMessage));
    }

    private String cropMessage(
            String data,
            boolean isMyMessage) {
        int max = isMyMessage ? 35 : 35 - username.length();
        if (data.length() < max) {
            return data;
        }
        return data.substring(0, max) + "...";
    }

    @Override
    public void timerCallback() {
        date.setText(DateTimeUtils.convertDate(dateInstance));
        lastMessage.applyCss();
        lastMessage.layout();
    }

    @Override
    protected Long getData() {
        return getID();
    }

    public long getID() {
        return ID;
    }
}
