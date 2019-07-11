package com.fagi.controller.contentList;

import com.fagi.action.Action;
import com.fagi.action.items.LoadFXML;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ContentItemController<S> extends HBox {
    @FXML Label date;
    @FXML Label lastMessage;
    @FXML private Label usernameLabel;
    @FXML private ImageView image;

    protected final String username;
    private final Action<S> action;
    protected Date dateInstance;

    public ContentItemController(
            String username,
            Date date,
            List<String> usernames,
            String fxmlResource,
            Action<S> action) {
        this.username = username;
        this.dateInstance = date;
        new LoadFXML(fxmlResource).execute(this);
        setUsers(usernames);
        this.action = action;
    }

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

    public Label getUserName() {
        return usernameLabel;
    }

    public void setDate(Date date) {
        dateInstance = date;
    }

    @FXML
    protected void openConversation() {
        action.execute(getData());
    }

    protected abstract S getData();
}
