/*
 * Copyright (c) 2016. Nicklas 'MiNiWolF' Pingel and Marcus 'Zargess' Haagh.
 */

package view;

import com.fagi.controller.MainScreen;
import com.fagi.controller.ContentController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Created by miniwolf on 08-07-2016.
 */
public class SearchContentTest extends Application {
    private Stage primaryStage;
    private Scene scene;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.UNDECORATED);

        scene = new Scene(new AnchorPane());
        showMainScreen();
        primaryStage.setTitle("Fagi Welcome");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showMainScreen() {
        MainScreen controller = new MainScreen("dimmer", null, primaryStage);
        FXMLLoader loader = new FXMLLoader(
                controller.getClass().getResource("/com/fagi/view/Main.fxml"));
        loader.setController(controller);
        try {
            scene.setRoot(loader.load());
        } catch (IOException e) {
            System.err.println(e.toString());
            return;
        }

        try {
            ContentController contentController = new ContentController();
            FXMLLoader contentLoader = new FXMLLoader(controller.getClass().getResource("/com/fagi/view/SearchContent.fxml"));
            contentLoader.setController(contentController);
            VBox searchContent = contentLoader.load();

            HBox searchContact = FXMLLoader.load(controller.getClass().getResource("/com/fagi/view/content/SearchContact.fxml"));
            HBox searchContact2 = FXMLLoader.load(controller.getClass().getResource("/com/fagi/view/content/SearchContact.fxml"));

            contentController.addToContentList(searchContact);
            contentController.addToContentList(searchContact2);

            controller.setScrollPaneContent(searchContent);
        } catch (IOException e) {
            e.printStackTrace();
        }


        controller.initCommunication();
        primaryStage.sizeToScene();
    }
}
