package com.fagi.controller.contentList;

import com.fagi.action.Action;
import com.fagi.conversation.Conversation;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.controller.contentList.ContentItemController;
import com.fagi.util.DateTimeUtils;
import javafx.fxml.FXML;

import java.util.Date;

/**
 * @author miniwolf
 */
public class MessageItemController extends ContentItemController {
    private static final String fxmlResource = "/view/content/ConversationItem.fxml";
    private final Action<Long> action;
    private long ID;

    public MessageItemController(
            String username,
            Conversation conversation,
            Action<Long> action,
            Date date) {
        super(username, date, conversation.getParticipants(), fxmlResource);
        this.ID = conversation.getId();
        this.action = action;
        getStyleClass().add("contact");

        if (conversation.getLastMessage() != null) {
            setLastMessage(conversation.getLastMessage());
        }
    }

    @FXML
    protected void openConversation() {
        action.execute(ID);
    }

    public void setLastMessage(TextMessage message) {
        String sender = message.getMessageInfo().getSender();
        boolean isMyMessage = sender.equals(username);
        String senderString = (isMyMessage ? "You" : sender);
        lastMessage.setText(senderString + ": " + cropMessage(message.getData(), isMyMessage));
    }

    private String cropMessage(String data, boolean isMyMessage) {
        int max = isMyMessage ? 35 : 35 - username.length();
        if (data.length() < max) {
            return data;
        }
        return data.substring(0, max) + "...";
    }

    public long getID() {
        return ID;
    }

    @Override
    protected void timerCallback() {
        date.setText(DateTimeUtils.convertDate(dateInstance));
        lastMessage.applyCss();
        lastMessage.layout();
    }
}
