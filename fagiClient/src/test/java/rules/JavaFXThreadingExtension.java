package rules;

import javafx.embed.swing.JFXPanel;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.swing.SwingUtilities;
import java.util.concurrent.CountDownLatch;

/**
 * A JUnit {@link BeforeAllCallback} for running tests on the JavaFX thread and performing
 * JavaFX initialisation.  To include in your test case, add the following code above the class declaration:
 * <p>
 * <pre>
 * {@literal @}ExtendsWith(JavaFXThreadingExtension.class)
 * </pre>
 *
 * @author Andy Till
 * @author miniwolf
 */
public class JavaFXThreadingExtension implements BeforeAllCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) throws Exception {
        var timeMillis = System.currentTimeMillis();

        final var latch = new CountDownLatch(1);

        SwingUtilities.invokeLater(() -> {
            // initializes JavaFX environment
            new JFXPanel();

            latch.countDown();
        });

        System.out.println("javafx initialising...");
        latch.await();
        System.out.println("javafx is initialised in " + (System.currentTimeMillis() - timeMillis) + "ms");
    }
}
