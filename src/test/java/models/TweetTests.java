package models;

import static org.junit.Assert.*;

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

    @Test
    public void mentionOne() {
        Tweet t = user.addTweet("@user");
        assertEquals(1, t.getMentioned().size());
    }

    @Test
    public void mentionTwo() {
        Tweet t = user.addTweet("@user1 @user2");
        assertEquals(2, t.getMentioned().size());
    }

    @Test
    public void tagOne() {
        Tweet t = user.addTweet("#tag");
        assertEquals(1, t.getHashtags().size());
    }

    @Test
    public void tagTwo() {
        Tweet t = user.addTweet("#tag1 #tag2");
        assertEquals(2, t.getHashtags().size());
    }

    @Test
    public void mixed() {
        Tweet t = user.addTweet("Doing #stuff with @user1 and @user2 #lookatmeusingtags #imsocool");
        assertEquals(2, t.getMentioned().size());
        assertEquals(3, t.getHashtags().size());
    }
}
