package com.fagi.model;

import java.io.Serializable;

/**
 * Created by Marcus on 08-07-2016.
 */
public record SearchUsersRequest(String sender, String searchString) implements Serializable {
}
