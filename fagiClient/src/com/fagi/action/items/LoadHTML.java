/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.utility.Logger;
import javafx.concurrent.Worker;
import javafx.scene.Parent;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;

import java.io.IOException;
import java.net.URL;

/**
 * Action will load the FXML from the resources onto the controller specified as a Parent element.
 * This requires the controller to be extending a subtype of parent.
 *
 * @author miniwolf
 */
public class LoadHTML implements Action {
    private final Parent parent;
    private WebEngine engine;
    private final String resourcePath;

    public LoadHTML(Parent parent, WebEngine engine, String resourcePath) {
        this.parent = parent;
        this.engine = engine;
        this.resourcePath = resourcePath;
    }

    @Override
    public void execute() {
        URL url = getClass().getResource(resourcePath);
        engine.load(url.toString());

        engine.getLoadWorker().stateProperty().addListener((observableValue, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject win = (JSObject) engine.executeScript("window");

                win.setMember("app", parent);
            }
        });
    }
}
