package com.fagi.controller.contentList;

import com.fagi.action.ActionableImpl;
import com.fagi.model.messages.message.TextMessage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author miniwolf
 */
public class MessageItemController extends ActionableImpl {
    @FXML private Label usernameLabel;
    @FXML private Label date;
    @FXML private Label lastMessage;
    private Date dateInstance;
    private final String username;
    private long ID;
    private boolean running = true;

    public MessageItemController(String username, long id) {
        this.username = username;
        ID = id;
    }

    @FXML
    public void initialize() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!running) {
                    this.cancel();
                }
                Platform.runLater(() -> date.setText(convertDate(dateInstance)));
            }
        }, 0, 1000);
    }

    public void setUsers(List<String> username) {
        this.usernameLabel.setText(String.join(", ", username));
    }

    public void setDate(Date date) {
        this.dateInstance = date;
    }

    private String convertDate(Date date) {
        if (date == null) {
            return "";
        }

        long now = new Date().getTime();
        long then = date.getTime();
        long diff = now - then;

        long diffDays = diff / (24 * 60 * 60 * 1000);
        if ( diffDays != 0 ) {
            SimpleDateFormat format = new SimpleDateFormat(diffDays > 365 ? "MM/dd/yyyy" : diffDays < 7 ? "EEEE" : "MMM d");
            return format.format(date);
        }

        long diffHours = diff / (60 * 60 * 1000) % 24;
        if ( diffHours != 0 ) {
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            return format.format(date);
        }

        long diffMinutes = diff / (60 * 1000) % 60;
        if ( diffMinutes != 0 ) {
            return Long.toString(diffMinutes) + " min";
        }
        return "now";
    }

    public void setLastMessage(TextMessage lastMessage) {
        String sender = lastMessage.getMessageInfo().getSender();
        String senderString = (sender.equals(username) ? "You" : sender);
        this.lastMessage.setText(senderString + ": " + lastMessage.getData());
    }

    @FXML
    public void openConversation() {
        action.execute();
    }

    public long getID() {
        return ID;
    }

    public void stopTimer() {
        this.running = false;
    }
}
