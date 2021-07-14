package com.fagi.model;

import java.io.Serializable;

/**
 * Created by costa on 11-12-2016.
 */
public record GetFriendListRequest(String sender) implements Serializable {
}
