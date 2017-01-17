package com.fagi.controller.contentList;

import com.fagi.action.Actionable;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.LoadFXML;
import com.fagi.conversation.Conversation;
import com.fagi.model.FriendRequest;
import com.fagi.model.messages.message.TextMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @author miniwolf
 */
public class MessageItemController extends HBox {
    @FXML private Label usernameLabel;
    @FXML private Label date;
    @FXML private Label lastMessage;
    @FXML private ImageView image;

    private Date dateInstance;
    private final String username;
    private long ID;
    private boolean running = true;
    private Actionable actionable = new ActionableImpl();

    private MessageItemController(String username, long ID) {
        this.username = username;
        this.ID = ID;

        new LoadFXML(this, "/com/fagi/view/content/Conversation.fxml").execute();
        getStyleClass().add("contact");
    }

    public MessageItemController(String username, Conversation conversation) {
        this(username, conversation.getId());
        setUsers(conversation.getParticipants());
        if (conversation.getLastMessage() != null) {
            setLastMessage(conversation.getLastMessage());
            setDate(conversation.getLastMessageDate());
        }
    }

    public MessageItemController(String username, FriendRequest request) {
        this(username, request.getMessage().getMessageInfo().getConversationID());
        List<String> list = new ArrayList<>();
        list.add(request.getFriendUsername());
        setUsers(list);
        setDate(request.getMessage().getMessageInfo().getTimestamp());
    }

    @FXML
    private void initialize() {
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!running) {
                    this.cancel();
                }
                Platform.runLater(() -> date.setText(convertDate(dateInstance)));
            }
        }, 0, 1000);
    }

    private String convertDate(Date date) {
        if (date == null) {
            return "";
        }

        long now = new Date().getTime();
        long then = date.getTime();
        long diff = now - then;

        long diffDays = diff / (24 * 60 * 60 * 1000);
        if (diffDays != 0) {
            SimpleDateFormat format = new SimpleDateFormat(
                diffDays > 365 ? "MM/dd/yyyy" : diffDays < 7 ? "EEE" : "MMM d");
            return format.format(date);
        }

        long diffHours = diff / (60 * 60 * 1000) % 24;
        if (diffHours != 0) {
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            return format.format(date);
        }

        long diffMinutes = diff / (60 * 1000) % 60;
        if (diffMinutes != 0) {
            return Long.toString(diffMinutes) + " min";
        }
        return "now";
    }

    @FXML
    private void openConversation() {
        actionable.execute();
    }

    public void setUsers(List<String> usernames) {
        List<String> meExcludedList = usernames.stream()
                                               .filter(name -> !name.equals(username))
                                               .collect(Collectors.toList());
        this.usernameLabel.setText(String.join(", ", meExcludedList));
        Image image = new Image(
                "/com/fagi/style/material-icons/" + meExcludedList.get(0).toCharArray()[0] + ".png", 46,
                46, true, true);
        this.image.setImage(image);
    }

    public void setLastMessage(TextMessage lastMessage) {
        String sender = lastMessage.getMessageInfo().getSender();
        String senderString = (sender.equals(username) ? "You" : sender);
        this.lastMessage.setText(senderString + ": " + lastMessage.getData());
    }

    public void setDate(Date date) {
        this.dateInstance = date;
    }

    public long getID() {
        return ID;
    }

    public void stopTimer() {
        this.running = false;
    }

    public Actionable getActionable() {
        return actionable;
    }
}
