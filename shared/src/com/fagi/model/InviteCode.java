package com.fagi.model;

import java.io.Serializable;

/**
 * Created by costa on 31-03-2017.
 */
public class InviteCode implements Serializable {
    private final int value;

    public InviteCode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
