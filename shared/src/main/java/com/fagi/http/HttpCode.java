package com.fagi.http;

// TODO: zargess - javadoc

/**
 * @author Marcus Haagh
 */
public enum HttpCode {
    OK(
            200,
            "200 OK"
    ),
    CREATED(
            201,
            "201 Created"
    ),
    BAD_REQUEST(
            400,
            "400 Bad Request"
    ),
    UNAUTHORIZED(
            401,
            "401 Unauthorized"
    ),
    FORBIDDEN(
            403,
            "403 Forbidden"
    ),
    NOT_FOUND(
            404,
            "404 Not Found"
    ),
    INTERNAL_SERVER_ERROR(
            500,
            "500 Internal Server Error"
    );

    private final int code;
    private final String responseText;

    HttpCode(
            int code,
            String responseText) {
        this.code = code;
        this.responseText = responseText;
    }

    public int getCode() {
        return code;
    }

    public String getResponseText() {
        return responseText;
    }
}
