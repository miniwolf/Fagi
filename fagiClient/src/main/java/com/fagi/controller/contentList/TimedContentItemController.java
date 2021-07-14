package com.fagi.controller.contentList;

import com.fagi.action.Action;
import javafx.application.Platform;

import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public abstract class TimedContentItemController<S> extends ContentItemController<S> {
    private Timer timer;

    public TimedContentItemController(
            String username,
            Date date,
            List<String> usernames,
            String fxmlResource,
            Action<S> action) {
        super(username, date, usernames, fxmlResource, action);
    }

    public void initialize() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> timerCallback());
            }
        }, 0, 1000);
    }

    public abstract void timerCallback();

    public void stopTimer() {
        timer.cancel();
    }
}
