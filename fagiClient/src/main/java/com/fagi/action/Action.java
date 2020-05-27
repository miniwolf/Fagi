package com.fagi.action;

/**
 * Created by miniwolf on 26-11-2016.
 */
public interface Action<T> {
    /**
     * This can be used to pass parameters that might be null at construction but has been created when this method is
     * called.
     *
     * @param t instance of generic type T
     */
    void execute(T t);
}
