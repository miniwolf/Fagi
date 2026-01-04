package com.fagi.http.server.exception;


import java.util.Set;

// TODO: zargess - javadoc
public class RestApiPathDuplicatedException extends RuntimeException {
    public RestApiPathDuplicatedException(Set<String> duplicatedPaths) {
        super("The following paths are duplicated on the rest api interfaces: " + duplicatedPaths);
    }
}
