package com.fagi.util;

import javafx.scene.text.Font;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import rules.JavaFXThreadingExtension;

class DisableOnLinuxAndMacCondition implements ExecutionCondition {
    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        if (isWindows()) {
            return ConditionEvaluationResult.enabled("Test enabled");
        } else {
            return ConditionEvaluationResult.disabled("Test disabled on mac and linux");
        }
    }

    private boolean isWindows() {
        return System
                .getProperty("os.name")
                .startsWith("Windows");
    }
}

/**
 * Testing that our current font returns the correct size for the surrounding textarea.
 * <p>
 * Values tested will be passed to the pref height and width.
 *
 * @author miniwolf
 */
@ExtendWith({JavaFXThreadingExtension.class, DisableOnLinuxAndMacCondition.class})
public class FontUtilsTest {
    private static final Font ROBOTO = new Font("Roboto-Regular", 13);

    @Test
    public void thirtyFourCharactersReturnsOneLine() {
        var message = "123456789 123456789 123456789 1234";
        Assertions.assertEquals(18, FontUtils.computeTextHeight(ROBOTO, message, 232), 0.001, "Should return one line");
    }

    @Test
    public void thirtyFiveCharactersReturnsTwoLines() {
        var message = "123456789 123456789 123456789 12345";
        Assertions.assertEquals(35,
                                FontUtils.computeTextHeight(ROBOTO, message, 232),
                                0.001,
                                "Should return two lines"
        );
    }

    @Test
    public void sixtyEightCharactersReturnsTwoLines() {
        var message = "123456789 123456789 123456789 1234 123456789 123456789 123456789 1234";
        Assertions.assertEquals(35,
                                FontUtils.computeTextHeight(ROBOTO, message, 232),
                                0.001,
                                "Should return two lines"
        );
    }

    @Test

    public void sixtyNineCharactersReturnsTwoLine() {
        var message = "123456789 123456789 123456789 1234 123456789 123456789 123456789 12345";
        Assertions.assertEquals(52,
                                FontUtils.computeTextHeight(ROBOTO, message, 232),
                                0.001,
                                "Should return three lines"
        );
    }

    @Test
    public void computeTextHeight() {
        var message = "123456789 123456789 123456789 1234";
        Assertions.assertEquals(228, FontUtils.computeTextWidth(ROBOTO, message, 232), 0.001, "Should return one line");
    }

    @Test
    public void computeTextHeightOverflow() {
        var message = "123456789 123456789 123456789 12345678";
        Assertions.assertEquals(232, FontUtils.computeTextWidth(ROBOTO, message, 232), 0.001, "Should return one line");
    }
}
