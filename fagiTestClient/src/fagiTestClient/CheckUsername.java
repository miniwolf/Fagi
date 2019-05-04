package fagiTestClient;

import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;

import java.util.regex.Pattern;

public class CheckUsername implements EventListener {
    private Element usernameField;
    private Element messageLabel;

    public CheckUsername(Element usernameField, Element messageLabel) {
        this.usernameField = usernameField;
        this.messageLabel = messageLabel;
    }

    public static boolean isValidUserName(String string) {
        return Pattern.matches("\\w*", string);
    }

    @Override
    public void handleEvent(Event evt) {
        checkValidUsername();
    }

    public void checkValidUsername() {
        var username = usernameField.getTextContent();
        if (username == null || username.trim().equals("")) {
            messageLabel.setTextContent("Username cannot be empty");
            return;
        }
        boolean valid = isValidUserName(username);

        if (!valid) {
            messageLabel.setTextContent("Username may not contain special symbols");
        }
    }
}
