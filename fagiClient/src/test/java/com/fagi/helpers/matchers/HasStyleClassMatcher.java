package com.fagi.helpers.matchers;

import javafx.scene.Node;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * This matcher is designed to check if a JavaFX Node object has a css class
 * @author zargess
 */
public class HasStyleClassMatcher extends TypeSafeMatcher<Node> {
    private final String styleClass;

    public HasStyleClassMatcher(String styleClass) {
        this.styleClass = styleClass;
    }

    @Override
    protected boolean matchesSafely(Node node) {
        return node.getStyleClass().contains(styleClass);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a Node with style class " + styleClass);
    }

    @Override
    protected void describeMismatchSafely(Node item, Description mismatchDescription) {
        mismatchDescription.appendText("was " + item.getStyleClass());
    }

    public static HasStyleClassMatcher hasStyleClass(String styleClass) {
        return new HasStyleClassMatcher(styleClass);
    }
}
