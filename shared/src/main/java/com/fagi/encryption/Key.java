package com.fagi.encryption;

import java.io.Serializable;

/**
 * Created by Marcus on 04-06-2016.
 */
public interface Key<T> extends Serializable {
    T key();
}
