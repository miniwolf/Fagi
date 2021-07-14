package com.fagi.model;

import java.io.Serializable;

/**
 * Created by costa on 07-12-2016.
 */
public record UserNameAvailableRequest(String username) implements Serializable {
}
