package com.fagi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by costa on 31-03-2017.
 */
public record InviteCodeContainer(List<Integer> codes) implements Serializable {
    public boolean contains(int inviteCode) {
        return codes.contains(inviteCode);
    }

    public void remove(int inviteCode) {
        codes.remove(inviteCode);
    }
}
