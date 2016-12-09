package com.fagi.action;

import java.util.HashMap;
import java.util.Map;

/**
 * @author miniwolf
 */
public class ActionableImpl implements Actionable {
    protected Action action;

    @Override
    public void assign(Action action) {
        this.action = action;
    }
}
