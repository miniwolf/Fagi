/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package com.fagi.controller.login;

import com.fagi.controller.Controller;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;

/**
 * LoginController is a common placeholder for the parameters and variables between the
 * different controllers that should be seamless.
 *
 * @author miniwolf
 */
public interface LoginController extends Controller {
    @FXML
    void next();

    @FXML
    void back();

    @FXML
    void handleQuit();

    @FXML
    void mousePressed(MouseEvent mouseEvent);

    @FXML
    void mouseDragged(MouseEvent mouseEvent);

    void setMessage(String message);

    String getMessageLabel();

    Parent getParentNode();
}
