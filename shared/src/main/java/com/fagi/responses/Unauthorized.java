package com.fagi.responses;

import java.io.Serial;

/**
 * We send this response object when the given user does not have authorization to
 * do this action
 * <p>
 * Created by Marcus on 07-07-2016.
 */
public class Unauthorized implements Response {
    @Serial
    private static final long serialVersionUID = 6L;
}
