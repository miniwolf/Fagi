package com.fagi.rest.api;

import com.fagi.conversation.ConversationType;
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
import com.fagi.model.Friend;
import com.fagi.model.messages.message.TextMessage;

import java.util.List;

@Path("/api/dummy")
public interface DummyRestApi {
    @Path("/hello-world")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.TEXT)
    String test();

    @Path("/int-test")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.TEXT)
    int intTest();

    @Path("/integer-test")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.TEXT)
    Integer integerTest();

    @Path("/boolean-test")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.TEXT)
    boolean booleanTest();

    @Path("/friends/{username}")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<Friend> getFriendType(
            @PathParam("username") String username,
            @QueryParam("online") Boolean online);

    @Path("/friends/{username}")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<Friend> getFriendType(
            @PathParam("username") String username,
            @HeaderParam("Stuff") String stuff);

    @Path("/friends/{username}")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<Friend> getFriendType(
            @PathParam("username") String username,
            @QueryParam("online") boolean online,
            @HeaderParam("Stuff") String stuff);

    @Path("/friends/{age}/{username}")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<Friend> getFriendType(
            @PathParam("age") int age,
            @PathParam("username") String username);

    @Path("/messages/text/{username}")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<TextMessage> getTextMessages(@PathParam("username") String username);

    @Path("/messages/text")
    @HttpMethod(HttpMethodType.POST)
    @Produces(MimeType.JSON)
    @Consumes(MimeType.JSON)
    TextMessage postMessage(@BodyParam TextMessage message);

    @Path("/conversations")
    @HttpMethod(HttpMethodType.GET)
    @Produces(MimeType.JSON)
    List<Integer> getConversationIdsOfType(
            @QueryParam("type") ConversationType type);
}
