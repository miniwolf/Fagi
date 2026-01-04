package com.fagi.http.rest.service;

import com.fagi.conversation.ConversationType;
import com.fagi.model.Friend;
import com.fagi.model.messages.message.TextMessage;
import com.fagi.rest.api.DummyRestApi;

import java.util.List;

// TODO: zargess - javadoc
public class DummyRestRestService implements DummyRestApi {
    @Override
    public String test() {
        return "Hello, world!";
    }

    @Override
    public int intTest() {
        return 42;
    }

    @Override
    public Integer integerTest() {
        return 42;
    }

    @Override
    public boolean booleanTest() {
        return true;
    }

    @Override
    public List<Friend> getFriendType(
            String username,
            Boolean online) {
        return List.of(new Friend(
                username,
                online
        ));
    }

    @Override
    public List<Friend> getFriendType(
            String username,
            String stuff) {
        return List.of(new Friend(
                username + "-" + stuff,
                false
        ));
    }

    @Override
    public List<Friend> getFriendType(
            String username,
            boolean online,
            String stuff) {
        return List.of(new Friend(
                username + "-" + stuff,
                online
        ));
    }

    @Override
    public List<Friend> getFriendType(
            int age,
            String username) {
        return List.of();
    }

    @Override
    public List<TextMessage> getTextMessages(String username) {
        return List.of();
    }

    @Override
    public TextMessage postMessage(TextMessage message) {
        return message;
    }

    @Override
    public List<Integer> getConversationIdsOfType(ConversationType type) {
        return switch (type) {
            case Multi -> List.of(
                    1,
                    2,
                    3
            );
            case Single -> List.of(
                    23,
                    42
            );
            case Placeholder -> List.of(10000);
        };
    }
}
