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
public class LoadFXML implements Action {
    private final Parent parent;
    private final String resourcePath;

    public LoadFXML(Parent parent, String resourcePath) {
        this.parent = parent;
        this.resourcePath = resourcePath;
    }

    @Override
    public void execute() {
        System.out.println("Creating loader");
        FXMLLoader loader = new FXMLLoader(getClass().getResource(resourcePath));
        System.out.println("Created loader");
        loader.setController(parent);
        System.out.println("the controller is set");
        loader.setRoot(parent);
        System.out.println("The parent is set");
        try {
            System.out.println("Attepmting to load");
            loader.load();
            System.out.println("Load successful");
        } catch (IOException ioe) {
            System.out.println("IO Wat");
            System.out.println(ioe.getStackTrace());
            ioe.printStackTrace();
            Logger.logStackTrace(ioe);
        } catch (Exception e) {
            System.out.println("Wat");
            e.printStackTrace();
            System.out.println(e.getStackTrace());
        }
    }
}
