package com.fagi.controller;

import com.fagi.action.Actionable;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.LoadFXML;
import com.fagi.action.items.OpenConversation;
import com.fagi.action.items.OpenInvitation;
import com.fagi.uimodel.FagiImage;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * @author miniwolf and zargess
 */
public class SearchContactController extends HBox {
    @FXML private Label userName;
    @FXML private ImageView image;

    private final boolean isFriend;
    private final MainScreen mainScreen;
    private final Actionable actionable = new ActionableImpl();

    public SearchContactController(boolean isFriend, MainScreen mainScreen,
                                   String username) {
        this.isFriend = isFriend;
        this.mainScreen = mainScreen;

        new LoadFXML(this, "/com/fagi/view/content/SearchContact.fxml").execute();
        Image image = new FagiImage(
                "/com/fagi/style/material-icons/" + Character.toUpperCase(username.toCharArray()[0])
                + ".png", 32, 32, true, true);
        this.image.setImage(image);
        userName.setText(username);
    }

    @FXML
    private void initialize() {
        actionable.assign(isFriend ? new OpenConversation(mainScreen, userName)
                                   : new OpenInvitation(mainScreen, userName.getText()));
    }

    @FXML
    private void openConversation() {
        actionable.execute();
    }
}
