package com.fagi.util;

import javafx.scene.text.Font;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rules.JavaFXThreadingRule;

import static org.junit.Assert.*;

/**
 * Created by miniwolf on 07-04-2017.
 */
public class UtilsTest {
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    private static final Font ROBOTO = new Font("Roboto-Regular", 13);

    @Test
    public void ThirtyFourCharactersReturnsOneLine() throws Exception {
        String message = "123456789 123456789 123456789 1234";
        Assert.assertEquals("Should return one line", 18,
                          Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void ThirtyFiveCharactersReturnsTwoLines() throws Exception {
        String message = "123456789 123456789 123456789 12345";
        Assert.assertEquals("Should return two lines", 35,
                            Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void SixtyEightCharactersReturnsTwoLines() throws Exception {
        String message = "123456789 123456789 123456789 1234 123456789 123456789 123456789 1234";
        Assert.assertEquals("Should return two lines", 35,
                            Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void SixtyNineCharactersReturnsTwoLine() throws Exception {
        String message = "123456789 123456789 123456789 1234 123456789 123456789 123456789 12345";
        Assert.assertEquals("Should return three lines", 52,
                            Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void computeTextHeight() throws Exception {
    }
}
