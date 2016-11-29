package com.fagi.controller.contentList;

import com.fagi.action.ActionableImpl;
import com.fagi.controller.MainScreen;
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
public class MessageItemController extends ActionableImpl<ItemActions> {
    @FXML private Label usernameLabel;
    @FXML private Label date;
    @FXML private Label lastMessage;
    private Date dateInstance;
    private final String username;

    public MessageItemController(MainScreen mainScreen, long ID, String username) {
        this.username = username;
        AddAction(ItemActions.OpenConversation, ItemActions.OpenConversationFromID(mainScreen, ID));
    }

    @FXML
    public void initialize() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
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

    public void setLastMessage(String lastMessage, String sender) {
        String senderString = (sender.equals(username) ? "You" : sender);
        this.lastMessage.setText(senderString + ": " + lastMessage);
    }

    @FXML
    public void openConversation() {
        ExecuteAction(ItemActions.OpenConversation);
    }
}
