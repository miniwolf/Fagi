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
public class ContactItemController extends TimedContentItemController<String> {
    @FXML private Pane status;

    private static final String fxmlResource = "/view/content/Contact.fxml";

    public ContactItemController(
            String myUsername,
            Friend contact,
            Action<String> action,
            Date date) {
        super(myUsername, date, new ArrayList<>() {{
            add(contact.username());
        }}, fxmlResource, action);
        toggleStatus(contact.online());
    }

    public void toggleStatus(boolean online) {
        if (online && !status
                .getStyleClass()
                .contains("pD")) {
            status
                    .getStyleClass()
                    .add("pD");
        } else {
            status
                    .getStyleClass()
                    .remove("pD");
        }
    }

    @Override
    public void timerCallback() {
        date.setText(DateTimeUtils.convertDate(dateInstance));
    }

    @Override
    protected String getData() {
        return getUserName().getText();
    }
}
