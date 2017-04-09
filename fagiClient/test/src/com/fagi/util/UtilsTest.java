package com.fagi.util;

import javafx.scene.text.Font;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import rules.JavaFXThreadingRule;

import static org.junit.Assert.*;

/**
 * Testing that our current font returns the correct size for the surrounding textarea.
 *
 * Values tested will be passed to the pref height and width.
 *
 * @author miniwolf
 */
public class UtilsTest {
    @Rule public JavaFXThreadingRule javafxRule = new JavaFXThreadingRule();
    private static final Font ROBOTO = new Font("Roboto-Regular", 13);

    @Test
    public void thirtyFourCharactersReturnsOneLine() throws Exception {
        String message = "123456789 123456789 123456789 1234";
        Assert.assertEquals("Should return one line", 18,
                          Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void thirtyFiveCharactersReturnsTwoLines() throws Exception {
        String message = "123456789 123456789 123456789 12345";
        Assert.assertEquals("Should return two lines", 35,
                            Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void sixtyEightCharactersReturnsTwoLines() throws Exception {
        String message = "123456789 123456789 123456789 1234 123456789 123456789 123456789 1234";
        Assert.assertEquals("Should return two lines", 35,
                            Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void sixtyNineCharactersReturnsTwoLine() throws Exception {
        String message = "123456789 123456789 123456789 1234 123456789 123456789 123456789 12345";
        Assert.assertEquals("Should return three lines", 52,
                            Utils.computeTextHeight(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void computeTextHeight() throws Exception {
        String message = "123456789 123456789 123456789 1234";
        Assert.assertEquals("Should return one line", 228,
                            Utils.computeTextWidth(ROBOTO, message, 232), 0.001);
    }

    @Test
    public void computeTextHeightOverflow() throws Exception {
        String message = "123456789 123456789 123456789 12345678";
        Assert.assertEquals("Should return one line", 232,
                            Utils.computeTextWidth(ROBOTO, message, 232), 0.001);
    }
}
