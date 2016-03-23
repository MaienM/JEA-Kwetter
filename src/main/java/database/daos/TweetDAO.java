package database.daos;

import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Named
@Stateless
public class TweetDAO extends BaseDAO<Tweet> {
    public long getCount() {
        return (long) em.createQuery("SELECT COUNT(tweet) FROM Tweet tweet").getSingleResult();
    }

    public List<Tweet> getRecent() {
        return em.createQuery("SELECT tweet FROM Tweet tweet ORDER BY tweet.id DESC").setMaxResults(50).getResultList();
    }

    public List<Tweet> getRecentByHashtag(Hashtag tag) {
        if (tag == null) return Collections.emptyList();
        return em.createQuery("SELECT tweet FROM Tweet tweet Join tweet.hashtags tag WHERE tag = :tag ORDER BY tweet.id DESC").setMaxResults(50).setParameter("tag", tag).getResultList();
    }

    public List<Tweet> getRecentOfFollows(User user) {
        if (user == null) return Collections.emptyList();
        return em.createQuery("SELECT tweet FROM Tweet tweet Join tweet.user.followers follower WHERE follower = :user ORDER BY tweet.id DESC").setMaxResults(50).setParameter("user", user).getResultList();
    }

    public List<Tweet> getRecentByMentioning(User user) {
        if (user == null) return Collections.emptyList();
        return em.createQuery("SELECT tweet FROM Tweet tweet Join tweet.mentioned mention WHERE mention = :user ORDER BY tweet.id DESC").setMaxResults(50).setParameter("user", user).getResultList();
    }

    public List<Tweet> findByText(String text) {
        return em.createQuery("SELECT tweet FROM Tweet tweet WHERE tweet.content LIKE :text ORDER BY tweet.id DESC").setMaxResults(50).setParameter("text", "%" + text + "%").getResultList();
    }
}
