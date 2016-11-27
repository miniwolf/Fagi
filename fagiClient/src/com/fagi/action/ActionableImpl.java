package com.fagi.action;

import java.util.HashMap;
import java.util.Map;

/**
 * @author miniwolf
 */
public class ActionableImpl<T> implements Actionable<T> {
    private Map<T, Handler> handlers = new HashMap<>();

    @Override
    public void AddAction(T actionName, Handler action) {
        handlers.put(actionName, action);
    }

    @Override
    public void ExecuteAction(T actionName) {
        handlers.get(actionName).execute();
    }
}
