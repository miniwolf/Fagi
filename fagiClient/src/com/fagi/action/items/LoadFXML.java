/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items;

import com.fagi.action.Action;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Action will load the FXML from the resources onto the controller specified as a Parent element.
 * This requires the controller to be extending a subtype of parent.
 *
 * @author miniwolf
 */
public class LoadFXML implements Action {
    private final Parent parent;
    private final String resourcePath;

    public LoadFXML(Parent parent, String resourcePath) {
        this.parent = parent;
        this.resourcePath = resourcePath;
    }

    @Override
    public void execute() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        loader.setController(parent);
        loader.setRoot(parent);
        try {
            loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
