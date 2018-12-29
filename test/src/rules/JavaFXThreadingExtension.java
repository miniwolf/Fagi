package rules;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

/**
 * A JUnit {@link Rule} for running tests on the JavaFX thread and performing
 * JavaFX initialisation.  To include in your test case, add the following code:
 * <p>
 * <pre>
 * {@literal @}Rule
 * public rules.JavaFXThreadingRule jfxRule = new rules.JavaFXThreadingRule();
 * </pre>
 *
 * @author Andy Till
 */
public class JavaFXThreadingExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        long timeMillis = System.currentTimeMillis();

        final CountDownLatch latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            // initializes JavaFX environment
            new JFXPanel();

            latch.countDown();
        });

        System.out.println("javafx initialising...");
        latch.await();
        System.out.println("javafx is initialised in "
                + (System.currentTimeMillis() - timeMillis) + "ms");
    }
}
