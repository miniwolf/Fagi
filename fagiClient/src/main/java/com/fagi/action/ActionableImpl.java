package com.fagi.action;

/**
 * responsible for storing and executing the action correctly.
 * TODO: Maybe we can just store an action, and define a setAction(Action actionName, Action actionName2).
 * TODO: In addition we could do addAction(String actionName, Action action) and the controller would know which to call.
 *
 * @author miniwolf
 */
public class ActionableImpl implements Actionable {
    protected Action action;

    @Override
    public void assign(Action action) {
        this.action = action;
    }

    @Override
    public void execute() {
        action.execute();
    }
}
