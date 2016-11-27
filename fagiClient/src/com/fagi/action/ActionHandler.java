package com.fagi.action;

import java.util.ArrayList;
import java.util.List;

/**
 * @author miniwolf
 */
public class ActionHandler implements Handler {
    private List<Action> actions = new ArrayList<>();

    @Override
    public void addAction(Action action) {
        actions.add(action);
    }

    @Override
    public void execute() {
        actions.forEach(Action::Execute);
    }
}
