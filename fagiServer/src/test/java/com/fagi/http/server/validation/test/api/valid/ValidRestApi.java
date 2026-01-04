package com.fagi.http.server.validation.test.api.valid;

import com.fagi.http.HttpMethodType;
import com.fagi.http.MimeType;
import com.fagi.http.annotation.Consumes;
import com.fagi.http.annotation.Creates;
import com.fagi.http.annotation.HttpMethod;
import com.fagi.http.annotation.Path;
import com.fagi.http.annotation.Produces;
import com.fagi.http.annotation.param.BodyParam;
import com.fagi.http.annotation.param.HeaderParam;
import com.fagi.http.annotation.param.PathParam;
import com.fagi.http.annotation.param.QueryParam;

import java.util.List;

@Path("/valid")
public interface ValidRestApi {
    @Path("/users/names")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<String> getAllUsernames();

    @Path("/users/{type}")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<String> getUsersOfType(@PathParam("type") String type);

    @Path("/users/names")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<String> getUsernamesOfUsersFromBeforeDate(@QueryParam("date") String date);

    @Path("/users/new")
    @HttpMethod(HttpMethodType.POST)
    @Consumes(MimeType.TEXT)
    @Produces(MimeType.TEXT)
    @Creates
    String createUser(
            @BodyParam String username,
            @HeaderParam("password") String password);

    @Path("/users/update")
    @HttpMethod(HttpMethodType.PUT)
    @Consumes(MimeType.TEXT)
    @Creates
    void updateUser(
            @BodyParam String username,
            @HeaderParam("password") String password);

    @Path("/users/{username}/{history}")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<String> getUserHistory(
            @PathParam("username") String username,
            @PathParam("history") String history,
            @QueryParam("age") int age,
            @QueryParam("status") String status,
            @QueryParam("accountAge") int accountAge,
            @HeaderParam("working") boolean working,
            @HeaderParam("return-all") boolean returnAll,
            @HeaderParam("timeoutms") long timeoutms,
            @HeaderParam("mediaTypeToInclude") String mediaTypeToInclude);
}
