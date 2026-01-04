package com.fagi.http;

/**
 * This enum is used to indicate what type of media a rest endpoint can consume and produce. The types should adhere
 * to the HTTP standard.
 * @author Marcus Haagh
 */
public enum MimeType {
    JSON("application/json"),
    TEXT("text/plain");

    private final String value;

    MimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
