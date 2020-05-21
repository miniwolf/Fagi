package com.fagi.util;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

/**
 * Created by miniwolf on 17-01-2017.
 */
public class FontUtils {
    private static Text helper = new Text();

    public static double computeTextWidth(
            Font font,
            String text,
            double wrappingWidth) {
        helper.setText(text);
        helper.setFont(font);
        // Note that the wrapping width needs to be set to zero before
        // getting the text's real preferred width.
        helper.setWrappingWidth(0);
        double w = Math.min(helper.prefWidth(-1), wrappingWidth);
        helper.setWrappingWidth((int) Math.ceil(w));
        return Math.ceil(helper
                                 .getLayoutBounds()
                                 .getWidth());
    }

    public static double computeTextHeight(
            Font font,
            String text,
            double wrappingWidth) {
        helper.setText(text);
        helper.setFont(font);
        helper.setWrappingWidth((int) wrappingWidth);
        return Math.ceil(helper
                                 .getLayoutBounds()
                                 .getHeight());
    }
}
