package com.fagi.controller.contentList;

import com.fagi.action.items.LoadFXML;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public abstract class ContentItemController extends HBox {
    protected final String username;
    protected Date dateInstance;
    @FXML Label date;
    @FXML Label lastMessage;
    @FXML private Label usernameLabel;
    @FXML private ImageView image;
    private Timer timer;

    public ContentItemController(String username, Date date, List<String> usernames, String fxmlResource) {
        this.username = username;
        dateInstance = date;
        new LoadFXML(fxmlResource).execute(this);
        setUsers(usernames);
    }

    @FXML
    private void initialize() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    timerCallback();
                });
            }
        }, 0, 1000);
    }

    protected abstract void timerCallback();

    protected void setUsers(List<String> usernames) {
        List<String> meExcludedList = usernames.stream()
                .filter(name -> !name.equals(username))
                .collect(Collectors.toList());
        this.usernameLabel.setText(String.join(", ", meExcludedList));
        Image image = new Image(
                "/style/material-icons/" + Character.toUpperCase(meExcludedList.get(0).toCharArray()[0]) + ".png",
                46,
                46,
                true,
                true);
        this.image.setImage(image);
    }

    public void stopTimer() {
        timer.cancel();
    }

    public Label getUserName() {
        return usernameLabel;
    }

    public void setDate(Date date) {
        dateInstance = date;
    }
}
