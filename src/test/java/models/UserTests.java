package models;

import static org.junit.Assert.*;

import database.models.Tweet;
import database.models.User;
import org.junit.Before;
import org.junit.Test;

public class UserTests {
    private User user;

    @Before
    public void setUp() {
        user = new User("test");
    }

    @Test
    public void addTweet() {
        Tweet t = user.addTweet("test");
        assertNotNull(t);
        assertTrue(user.getTweets().contains(t));
    }
}
