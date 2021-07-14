package com.fagi.model;

import com.fagi.encryption.AESKey;

import java.io.Serializable;

/**
 * Created by Marcus on 05-06-2016.
 */
public record Session(AESKey key) implements Serializable {
}