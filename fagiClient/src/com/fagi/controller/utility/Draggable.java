/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.utility;

import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * Created by miniwolf on 05-07-2016.
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
