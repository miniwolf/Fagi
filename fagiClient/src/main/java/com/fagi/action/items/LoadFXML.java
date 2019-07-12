/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.utility.Logger;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Action will load the FXML from the resources onto the controller specified as a Parent element.
 * This requires the controller to be extending a subtype of parent.
 *
 * @author miniwolf
 */
public class LoadFXML implements Action<Parent> {
    private final String resourcePath;

    public LoadFXML(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    @Override
    public void execute(Parent parent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        loader.setController(parent);
        loader.setRoot(parent);
        try {
            loader.load();
        } catch (IOException ioe) {
            ioe.printStackTrace();
            Logger.logStackTrace(ioe);
        }
    }
}
