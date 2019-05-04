package com.fagi.controller;

public class MissingElement extends Exception {
    public MissingElement(String identifierId, String resourcePath) {
        super(resourcePath + ": could not locate id: " + identifierId);
    }
}
