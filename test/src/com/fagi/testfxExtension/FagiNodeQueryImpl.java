package com.fagi.testfxExtension;

import javafx.scene.Node;
import org.testfx.service.query.EmptyNodeQueryException;
import org.testfx.service.query.impl.NodeQueryImpl;

public class FagiNodeQueryImpl extends NodeQueryImpl {
    @Override
    public <T extends Node> T query() {
        try {
            return super.query();
        } catch (EmptyNodeQueryException e) {
            var message = toString();
            throw new AssertionError("Could not find element: " +
                    message.substring(message.indexOf("lookup by selector: ") + "lookup by selector: ".length()));
        }
    }
}
