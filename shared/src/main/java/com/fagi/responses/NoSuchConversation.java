package com.fagi.responses;

import java.io.Serial;

/**
 * We send this respone object when the user tries to interact with a conversation that
 * does not exist
 * <p>
 * Created by Marcus on 07-07-2016.
 */
public class NoSuchConversation implements Response {
    @Serial
    private static final long serialVersionUID = 3L;
}
