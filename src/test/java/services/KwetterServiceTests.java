package services;

import static org.junit.Assert.*;

import database.models.Tweet;
import database.models.User;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;

@RunWith(CdiRunner.class)
public class KwetterServiceTests {
    private User user;

    @Inject
    private KwetterService service;

    @PersistenceContext
    private EntityManager em;

    @Produces
    private EntityManager createEntityManager() {
        if (em == null) {
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("...");
            em = emf.createEntityManager();
        }
        return em;
    }

    @Before
    public void setUp() {
        user = service.createUser("test");
    }

    @Test
    public void mentionOne() {
        service.createUser("user");
        Tweet t = service.createTweet(user, "@user");
        assertEquals(1, t.getMentioned().size());
    }

    @Test
    public void mentionTwo() {
        service.createUser("user1");
        service.createUser("user2");
        Tweet t = service.createTweet(user, "@user1 @user2");
        assertEquals(2, t.getMentioned().size());
    }

    @Test
    public void mentionNonExistant() {
        Tweet t = service.createTweet(user, "@user");
        assertEquals(0, t.getMentioned().size());
    }

    @Test
    public void tagOne() {
        Tweet t = service.createTweet(user, "#tag");
        assertEquals(1, t.getHashtags().size());
    }

    @Test
    public void tagTwo() {
        Tweet t = service.createTweet(user, "#tag1 #tag2");
        assertEquals(2, t.getHashtags().size());
    }

    @Test
    public void mixed() {
        service.createUser("user1");
        service.createUser("user2");
        Tweet t = service.createTweet(user, "Doing #stuff with @user1 and @user2 #lookatmeusingtags #imsocool");
        assertEquals(2, t.getMentioned().size());
        assertEquals(3, t.getHashtags().size());
    }
}
