package services;

import database.models.Tweet;
import database.models.User;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.Spy;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

import static org.junit.Assert.assertEquals;

@RunWith(CdiRunner.class)
public class KwetterServiceTests {
    private User user;

    @Inject private KwetterService service;

    @PersistenceContext
    private EntityManager em;

    @Produces
    private EntityManager createEntityManager() {
        if (em == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("test");
            em = emf.createEntityManager();
        }
        return em;
    }

    @Before
    public void setUp() {
        user = service.createUser("user");
    }

    @Test
    public void getTweetCount() {
        for (int i = 0; i < 345; i++) service.createTweet(user, "Lorem ipsum");
        assertEquals(345, service.getTweetCount());
    }

    @Test
    public void getRecentTweets() {
        for (int i = 0; i < 5; i++) service.createTweet(user, "Lorem ipsum");
        assertEquals(5, service.getRecentTweets().size());
    }

    @Test
    public void getRecentTweetsLimit() {
        for (int i = 0; i < 100; i++) service.createTweet(user, "Lorem ipsum");
        assertEquals(50, service.getRecentTweets().size());
    }

    @Test
    public void getRecentTweetsHashtag() {
        for (int i = 0; i < 5; i++) service.createTweet(user, "Lorem ipsum");
        for (int i = 0; i < 5; i++) service.createTweet(user, "Lorem #ipsum");
        assertEquals(5, service.getRecentTweetsHashtag("ipsum").size());
    }

    @Test
    public void getRecentTweetsHashtagLimit() {
        for (int i = 0; i < 100; i++) service.createTweet(user, "Lorem #ipsum");
        assertEquals(50, service.getRecentTweetsHashtag("ipsum").size());
    }

    @Test
    public void getRecentTweetsFollows() {
        User poster1 = service.createUser("poster1");
        User poster2 = service.createUser("poster2");
        service.addFollow(user, poster1);
        service.addFollow(user, poster2);

        for (int i = 0; i < 5; i++) service.createTweet(user, "Lorem ipsum");
        for (int i = 0; i < 5; i++) service.createTweet(poster1, "Lorem ipsum");
        for (int i = 0; i < 5; i++) service.createTweet(poster2, "Lorem ipsum");
        assertEquals(10, service.getRecentTweetsFollows(user).size());
    }

    @Test
    public void getRecentTweetsFollowsLimit() {
        User poster1 = service.createUser("poster1");
        User poster2 = service.createUser("poster2");
        service.addFollow(user, poster1);
        service.addFollow(user, poster2);

        for (int i = 0; i < 50; i++) service.createTweet(poster1, "Lorem ipsum");
        for (int i = 0; i < 50; i++) service.createTweet(poster2, "Lorem ipsum");
        assertEquals(50, service.getRecentTweetsFollows(user).size());
    }

    @Test
    public void getRecentTweetsMentioning() {
        User user2 = service.createUser("user2");
        for (int i = 0; i < 5; i++) service.createTweet(user, "Lorem ipsum");
        for (int i = 0; i < 5; i++) service.createTweet(user, "Lorem ipsum @user");
        for (int i = 0; i < 5; i++) service.createTweet(user2, "Lorem ipsum @user");
        assertEquals(10, service.getRecentTweetsMentioning(user).size());
    }

    @Test
    public void getRecentTweetsMentioningLimit() {
        User user2 = service.createUser("user2");
        for (int i = 0; i < 50; i++) service.createTweet(user, "Lorem ipsum @user");
        for (int i = 0; i < 50; i++) service.createTweet(user2, "Lorem ipsum @user");
        assertEquals(50, service.getRecentTweetsMentioning(user).size());
    }

    @Test
    public void findTweetsByText() {
        for (int i = 0; i < 5; i++) service.createTweet(user, "Lorem ipsum");
        for (int i = 0; i < 5; i++) service.createTweet(user, "Dolor sit amet");
        assertEquals(5, service.findTweetsByText("ipsum").size());
    }

    @Test
    public void findTweetsByTextLimit() {
        for (int i = 0; i < 100; i++) service.createTweet(user, "Lorem ipsum");
        assertEquals(50, service.findTweetsByText("ipsum").size());
    }

    @Test
    public void createTweetMentionOne() {
        service.createUser("newuser");
        Tweet t = service.createTweet(user, "@newuser");
        assertEquals(1, t.getMentioned().size());
    }

    @Test
    public void createTweetMentionTwo() {
        User u1 = service.createUser("user1");
        User u2 = service.createUser("user2");
        Tweet t = service.createTweet(user, "@user1 @user2");
        assertEquals(2, t.getMentioned().size());

        em.refresh(u1);
        assertEquals(1, u1.getMentions().size());
    }

    @Test
    public void createTweetMentionNonExistant() {
        Tweet t = service.createTweet(user, "@fakeuser");
        assertEquals(0, t.getMentioned().size());
    }

    @Test
    public void createTweetTagOne() {
        Tweet t = service.createTweet(user, "#tag");
        assertEquals(1, t.getHashtags().size());
    }

    @Test
    public void createTweetTagTwo() {
        Tweet t = service.createTweet(user, "#tag1 #tag2");
        assertEquals(2, t.getHashtags().size());
    }

    @Test
    public void createTweetMixed() {
        service.createUser("user1");
        service.createUser("user2");
        Tweet t = service.createTweet(user, "Doing #stuff with @user1 and @user2 #lookatmeusingtags #imsocool");
        assertEquals(2, t.getMentioned().size());
        assertEquals(3, t.getHashtags().size());
    }

    @Test
    public void getFollows() {
        User poster = service.createUser("poster");
        user.getFollowing().add(poster);
        assertEquals(1, service.getFollows(user).size());
    }

    @Test
    public void addFollow() {
        User poster = service.createUser("poster");
        service.addFollow(user, poster);
        assertEquals(1, user.getFollowing().size());
    }

    @Test
    public void removeFollow() {
        User poster = service.createUser("poster");
        user.getFollowing().add(poster);
        service.removeFollow(user, poster);
        assertEquals(0, user.getFollowing().size());
    }
}
