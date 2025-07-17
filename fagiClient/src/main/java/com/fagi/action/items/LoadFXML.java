/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.action.items;

import com.fagi.action.Action;
import com.fagi.logging.FagiLogger;
import com.fagi.logging.FagiLoggerFactory;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

/**
 * Action will load the FXML from the resources onto the controller specified as a Parent element.
 * This requires the controller to be extending a subtype of parent.
 *
 * @author miniwolf
 */
public record LoadFXML(String resourcePath) implements Action<Parent> {
    private static final FagiLogger LOGGER = FagiLoggerFactory.createLogger(LoadFXML.class);

    @Override
    public void execute(Parent parent) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        loader.setController(parent);
        loader.setRoot(parent);
        try {
            loader.load();
        } catch (IOException ioe) {
            LOGGER.error(
                    ioe,
                    () -> "Failed to load the FXML file: " + resourcePath
            );
        }
    }
}
