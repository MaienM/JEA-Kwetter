package jms;

import database.models.User;

import java.io.Serializable;

public class TweetMessage implements Serializable {
    private String content;
    private String username;

    public TweetMessage(User user, String content) {
        this.username = user.getUsername();
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getUsername() {
        return username;
    }
}
