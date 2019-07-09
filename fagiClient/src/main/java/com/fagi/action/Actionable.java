package com.fagi.action;

/**
 * Created by miniwolf on 26-11-2016.
 */
public interface Actionable {
    void assign(Action action);

    void execute();
}
