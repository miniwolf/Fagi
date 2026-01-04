package com.fagi.http.annotation;

import com.fagi.http.MimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// TODO: zargess - javadoc

/**
 * @author Marcus Haagh
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Consumes {
    MimeType value();
}
