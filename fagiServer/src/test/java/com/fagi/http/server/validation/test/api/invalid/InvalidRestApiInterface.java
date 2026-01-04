package com.fagi.http.server.validation.test.api.invalid;

import com.fagi.http.HttpMethodType;
import com.fagi.http.MimeType;
import com.fagi.http.annotation.Consumes;
import com.fagi.http.annotation.HttpMethod;
import com.fagi.http.annotation.Path;
import com.fagi.http.annotation.Produces;
import com.fagi.http.annotation.param.BodyParam;
import com.fagi.http.annotation.param.HeaderParam;
import com.fagi.http.annotation.param.PathParam;
import com.fagi.http.annotation.param.QueryParam;

import java.util.List;

public interface InvalidRestApiInterface {
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.TEXT)
    String missingPath();

    @Path("/mhm")
    String missingHttpMethod();

    @Path("/get-with-void-return")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.TEXT)
    void getWithVoidReturn();

    @Path("/no-produces")
    @HttpMethod(HttpMethodType.GET)
    String getMissingProduces();

    @Path("/getwithwrongreturntype")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.TEXT)
    List<String> getWithWrongReturnType();

    @Path("/getwithbody")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    Object getWithBody(
            @BodyParam Object body,
            @QueryParam("test") String test);

    @Path("/postwithnoproduces")
    @HttpMethod(HttpMethodType.POST)
    @Consumes(MimeType.JSON)
    Object postWithNoProduces(@BodyParam Object body);

    @Path("/postwithnobodyparam")
    @HttpMethod(HttpMethodType.POST)
    @Produces(MimeType.JSON)
    @Consumes(MimeType.JSON)
    Object postWithNoBodyParam(@QueryParam("test") String test);

    @Path("/postwithtwobodyparams")
    @HttpMethod(HttpMethodType.POST)
    @Produces(MimeType.JSON)
    @Consumes(MimeType.JSON)
    Object postWithTwoBodyParams(
            @BodyParam Object body1,
            @BodyParam Object body2);

    @Path("/postwithnoproduces")
    @HttpMethod(HttpMethodType.POST)
    @Produces(MimeType.JSON)
    Object postWithNoConsumes(@BodyParam Object body);

    @Path("/postwithmismatchreturntype")
    @HttpMethod(HttpMethodType.POST)
    @Produces(MimeType.TEXT)
    @Consumes(MimeType.TEXT)
    String postWithMismatchInputType(@BodyParam List<String> body);

    @Path("/putmissingproduceswhenreturnisnotvoid")
    @HttpMethod(HttpMethodType.PUT)
    @Consumes(MimeType.JSON)
    String putMissingProducesWhenReturnIsVoid(@BodyParam Object body);

    @Path("/putwithmismatchreturntype")
    @HttpMethod(HttpMethodType.PUT)
    @Produces(MimeType.TEXT)
    @Consumes(MimeType.TEXT)
    List<String> putWithMismatchReturnType(@BodyParam String body);

    @Path("/putwithproduceswhenvoidreturntype")
    @HttpMethod(HttpMethodType.PUT)
    @Produces(MimeType.TEXT)
    @Consumes(MimeType.TEXT)
    void putWithProducesWhenVoidReturnType(@BodyParam String body);

    @Path("/putwithmultiplebodyparams")
    @HttpMethod(HttpMethodType.PUT)
    @Consumes(MimeType.TEXT)
    void putWithMultipleBodyParams(
            @BodyParam String body1,
            @BodyParam String body2);

    @Path("/putmissingbody")
    @HttpMethod(HttpMethodType.PUT)
    @Consumes(MimeType.TEXT)
    void putWithMissingBody(@QueryParam("test") String test);

    @Path("/putmissingconsumes")
    @HttpMethod(HttpMethodType.PUT)
    void putMissingConsumes(@BodyParam Object body);

    @Path("/putmismatchbodytype")
    @HttpMethod(HttpMethodType.PUT)
    @Consumes(MimeType.TEXT)
    void putMismatchBodyType(@BodyParam List<String> body);

    @Path("/deletewithmissingproduces")
    @HttpMethod(HttpMethodType.DELETE)
    String deleteWithMissingProduces();

    @Path("/deletewithproduceswhenvoidreturntype")
    @HttpMethod(HttpMethodType.DELETE)
    @Produces(MimeType.TEXT)
    void deleteWithProducesWhenVoidReturnType();

    @Path("/deletewithmultiplebodyparams")
    @HttpMethod(HttpMethodType.DELETE)
    @Consumes(MimeType.TEXT)
    void deleteWithMultipleBodyParams(
            @BodyParam String body1,
            @BodyParam String body2);

    @Path("/deletemissingbody")
    @HttpMethod(HttpMethodType.DELETE)
    @Consumes(MimeType.TEXT)
    void deleteWithMissingBody(@QueryParam("test") String test);

    @Path("/deletemissingconsumes")
    @HttpMethod(HttpMethodType.DELETE)
    void deleteWithMissingConsumes(@BodyParam Object body);

    @Path("/deletewithmismatchreturntype")
    @HttpMethod(HttpMethodType.DELETE)
    @Produces(MimeType.TEXT)
    List<String> deleteWithMismatchReturnType();

    @Path("/deletemismatchbodytype")
    @HttpMethod(HttpMethodType.DELETE)
    @Consumes(MimeType.TEXT)
    void deleteMismatchBodyType(@BodyParam List<String> body);

    @Path("/path/with/duplicate/tempalate/params/{test}/{test}")
    @HttpMethod(HttpMethodType.DELETE)
    void pathWithDuplicateTemplateParams(@PathParam("test") String test);

    @Path("/methodwithduplicatepathparams/{test}")
    @HttpMethod(HttpMethodType.DELETE)
    void methodWithDuplicatePathParams(
            @PathParam("test") String test1,
            @PathParam("test") String test2);

    @Path("/methodwithmissingpathparam/{test}/{other}")
    @HttpMethod(HttpMethodType.DELETE)
    void methodWithMissingPathParam(@PathParam("test") String test);

    @Path("/methodwithpathparamnotinpath")
    @HttpMethod(HttpMethodType.DELETE)
    void methodWithPathParamNotInPath(@PathParam("test") String test);

    @Path("/methodwithqueryparamwithcomplextype")
    @HttpMethod(HttpMethodType.DELETE)
    void methodWithQueryParamWithComplexType(@QueryParam("ids") List<String> ids);

    @Path("/methodwithheaderparamwithcomplextype")
    @HttpMethod(HttpMethodType.DELETE)
    void methodWithHeaderParamWithComplexType(@HeaderParam("ids") List<String> ids);
}
