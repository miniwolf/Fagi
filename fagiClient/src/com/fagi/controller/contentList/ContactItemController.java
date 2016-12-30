package com.fagi.controller.contentList;

import com.fagi.action.Actionable;
import com.fagi.action.ActionableImpl;
import com.fagi.action.items.LoadFXML;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    }

    public Label getUserName() {
        return userName;
    }

    public Actionable getActionable() {
        return actionable;
    }
}
