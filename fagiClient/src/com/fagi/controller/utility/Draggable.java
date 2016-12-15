/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.utility;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * To be able to drag a window element around, we need to access the primary stage.
 * Here we use the event from the mouse to offset the element.
 *
 * @author miniwolf
 */
public class Draggable {
    private double xOffset;
    private double yOffset;
    private Stage primaryStage;

    public Draggable(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void mousePressed(MouseEvent mouseEvent) {
        xOffset = primaryStage.getX() - mouseEvent.getScreenX();
        yOffset = primaryStage.getY() - mouseEvent.getScreenY();
    }

    public void mouseDragged(MouseEvent mouseEvent) {
        primaryStage.setX(mouseEvent.getScreenX() + xOffset);
        primaryStage.setY(mouseEvent.getScreenY() + yOffset);
    }
}
