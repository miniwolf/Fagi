package com.fagi.controller;

import com.fagi.controller.utility.Draggable;
import com.fagi.main.FagiApp;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Created by Sidheag on 2016-07-08.
 */
public abstract class ScreenController {
    protected FagiApp fagiApp;
    protected final Draggable draggable;
    protected final String configFileLocation;
    protected MasterController parentController;
    protected Stage primaryStage;

    public ScreenController(FagiApp fagiApp, String configFileLocation, Stage primaryStage, MasterController parentController) {
        this.fagiApp = fagiApp;
        this.configFileLocation = configFileLocation;
        draggable = new Draggable(primaryStage);
        this.parentController = parentController;
        this.primaryStage = primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }
}
