package com.fagi.controller.contentList;

import com.fagi.action.Actionable;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.LoadFXML;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

/**
 * @author miniwolf and zargess
 */
public class ContactItemController extends HBox {
    @FXML private Label userName;
    @FXML private Label date;
    @FXML private Label lastMessage;
    @FXML private Pane status;
    @FXML private ImageView image;

    private final Actionable actionable = new ActionableImpl();

    public ContactItemController() {
        new LoadFXML(this, "/com/fagi/view/content/Contact.fxml").execute();
    }

    @FXML
    private void openConversation() {
        actionable.execute();
    }

    public void toggleStatus(boolean online) {
        if (online && !status.getStyleClass().contains("pD")) {
            status.getStyleClass().add("pD");
        } else {
            status.getStyleClass().remove("pD");
        }
    }

    public void setUserName(String userName) {
        this.userName.setText(userName);
        Image image = new Image(
                "/com/fagi/style/material-icons/" + userName.toCharArray()[0] + ".png", 46, 46,
                true, true);
        this.image.setImage(image);
    }

    public Label getUserName() {
        return userName;
    }

    public Actionable getActionable() {
        return actionable;
    }
}
