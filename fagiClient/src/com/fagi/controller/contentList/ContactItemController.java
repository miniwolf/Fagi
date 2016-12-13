package com.fagi.controller.contentList;

import com.fagi.action.ActionableImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

/**
 * @author miniwolf and zargess
 */
public class ContactItemController extends ActionableImpl {
    @FXML private Label userName;
    @FXML private Label date;
    @FXML private Label lastMessage;
    @FXML private Pane status;

    public void setUserName(String userName) {
        this.userName.setText(userName);
    }

    public Label getUserName() {
        return userName;
    }

    @FXML
    public void openConversation() {
        action.execute();
    }

    public void toggleStatus(boolean online) {
        if (online && !status.getStyleClass().contains("pD")) {
            status.getStyleClass().add("pD");
        } else {
            status.getStyleClass().remove("pD");
        }
    }
}
