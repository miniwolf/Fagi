package com.fagi.action;

/**
 * Created by miniwolf on 26-11-2016.
 */
public interface Actionable<T> {
    void AddAction(T actionName, Handler action);
    void ExecuteAction(T actionName);
}
