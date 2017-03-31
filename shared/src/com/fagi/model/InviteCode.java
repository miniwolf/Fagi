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

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof InviteCode)) return false;

        InviteCode other = (InviteCode)obj;

        return other.getValue() == this.value;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + 31 * value;
    }
}
