package fagiTestClient;

import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import org.w3c.dom.events.EventTarget;

import java.net.URL;

public class LoginSystem {
    private WebEngine engine;

    public LoginSystem(WebEngine engine) {
        this.engine = engine;
    }

    public static void initialize(WebEngine engine) {
        var controller = new LoginSystem(engine);
        engine.getLoadWorker().stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject win = (JSObject) engine.executeScript("window");
                win.setMember("app", controller);

                var document = engine.getDocument();
                var usernameMessageField = document.getElementById("usernameLabel");
                var usernameField = document.getElementById("identifierId");
                ((EventTarget) usernameField).addEventListener("focusout", new CheckUsername(usernameField, usernameMessageField), false);
            }
        });
        controller.loadPage();
    }

    void loadPage() {
        URL url = getClass().getResource("/fagiTestClient/view/LoginScreen.html");
        engine.load(url.toString());
    }

    public void Create() {
        System.out.println("Create Account");
        CreateUserSystem.initialize(engine);
    }

    public void Login() {
        System.out.println("Login");
    }

    public void checkusername(String c) {
        System.out.println(c);
    }
}
