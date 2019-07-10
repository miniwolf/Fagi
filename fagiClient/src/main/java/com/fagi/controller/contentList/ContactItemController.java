package com.fagi.controller.contentList;

import com.fagi.action.Action;
import com.fagi.model.Friend;
import com.fagi.util.DateTimeUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author miniwolf and zargess
 */
public class ContactItemController extends ContentItemController {
    @FXML private Pane status;

    private static final String fxmlResource = "/com/fagi/view/content/Contact.fxml";

    private final Action<String> action;

    public ContactItemController(
            String myUsername,
            Friend contact,
            Action<String> action,
            Date date) {
        super(myUsername, date, new ArrayList<>() {{
            add(contact.getUsername());
        }}, fxmlResource);
        this.action = action;
        toggleStatus(contact.isOnline());
    }

    public void toggleStatus(boolean online) {
        if (online && !status.getStyleClass().contains("pD")) {
            status.getStyleClass().add("pD");
        } else {
            status.getStyleClass().remove("pD");
        }
    }

    @Override
    protected void timerCallback() {
        date.setText(DateTimeUtils.convertDate(dateInstance));
    }

    @FXML
    protected void openConversation() {
        action.execute(getUserName().getText());
    }
}
