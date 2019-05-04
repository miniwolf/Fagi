package fagiTestClient;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.w3c.dom.events.EventTarget;

import java.net.URL;

public class CreateUserSystem {
    public CreateUserSystem() {
    }

    public static void initialize(WebEngine engine) {
        var controller = new CreateUserSystem();
        engine.getLoadWorker().stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject win = (JSObject) engine.executeScript("window");
                win.setMember("app", controller);

                var document = engine.getDocument();
                var usernameMessageField = document.getElementById("usernameLabel");
                var usernameField = document.getElementById("identifierId");
            }
        });

        controller.loadPage(engine);
    }

    private void loadPage(WebEngine engine) {
        URL url = getClass().getResource("/fagiTestClient/view/CreateUserNameAndPassword.html");
        engine.load(url.toString());
    }

    public void checkUsername(String content) {
        if (!CheckUsername.isValidUserName(content)) {

        }
        if (!CheckUsername.)
    }
}
