package com.fagi.http.annotation;

import com.fagi.http.HttpMethodType;
import com.fagi.http.MimeType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>This annotation is used to indicate what the media type a rest endpoint produces as return value.</p>
 * <p><b>OBS:</b> This annotation is required for all rest endpoints marked as GET requests.</p>
 *
 * @author Marcus Haagh
 * @see HttpMethodType#GET
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Produces {
    MimeType value();
}
