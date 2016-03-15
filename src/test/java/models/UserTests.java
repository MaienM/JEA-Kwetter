package models;

import static org.junit.Assert.*;

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

    @Test
    public void addFollowing() {
        User u2 = new User("test2");
        user.addFollowing(u2);
        assertTrue(user.getFollowing().contains(u2));
        assertTrue(u2.getFollowers().contains(user));
    }
}
