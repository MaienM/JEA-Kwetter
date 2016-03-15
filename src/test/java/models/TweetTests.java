package models;

import database.models.User;
import org.junit.Before;
import org.junit.Test;

public class TweetTests {
    private User user;

    @Before
    public void setUp() {
        user = new User("test");
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxLength() {
        user.addTweet("This tweet is way, way too long, and should thus not be allowed. This is quite important, as this is the entire point of Twitter, even though they seem to not realize this themselves, considering the talk of upping the limit to 10,000!");
    }
}
