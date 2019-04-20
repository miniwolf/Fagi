package fagiTestClient;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class JavaFXWebView extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }

    @Override
    public void start(final Stage stage)
    {
        // Create the WebView
        WebView webView = new WebView();

        // Create the WebEngine
        final WebEngine webEngine = webView.getEngine();

        // LOad the Start-Page
        webEngine.load("http://www.oracle.com");

        // Update the stage title when a new web page title is available
        webEngine.getLoadWorker().stateProperty().addListener((ov, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED)
            {
                //stage.setTitle(webEngine.getLocation());
                stage.setTitle(webEngine.getTitle());
            }
        });

        // Create the VBox
        VBox root = new VBox();
        // Add the WebView to the VBox
        root.getChildren().add(webView);

        // Set the Style-properties of the VBox
        root.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");

        // Create the Scene
        Scene scene = new Scene(root);
        // Add  the Scene to the Stage
        stage.setScene(scene);
        // Display the Stage
        stage.show();
    }
}