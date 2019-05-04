package com.fagi.login;

public enum CheckUsernameResult {
    Empty("Username cannot be empty"),
    InvalidCharacters("Username may not contain special symbols"),
    AlreadyInUse("Username is not available"),
    Valid(""),
    ;

    private String errorMessage;

    CheckUsernameResult(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
