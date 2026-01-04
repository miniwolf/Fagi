package com.fagi.http.server.validation.test.api.invalid;

import com.fagi.http.annotation.Path;

@Path("/path")
public interface RestApiWithInvalidMethod {
    void invalidApiMethod();
}
