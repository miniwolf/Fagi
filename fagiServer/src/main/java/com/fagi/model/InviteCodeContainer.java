package com.fagi.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by costa on 31-03-2017.
 */
public record InviteCodeContainer(List<InviteCode> codes) implements Serializable {
    public boolean contains(InviteCode inviteCode) {
        return codes.contains(inviteCode);
    }

    public void remove(InviteCode inviteCode) {
        codes.remove(inviteCode);
    }
}
