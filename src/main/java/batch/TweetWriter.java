package batch;

import database.daos.HashtagDAO;
import database.daos.UserDAO;
import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;

import javax.batch.api.chunk.ItemWriter;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweetWriter implements ItemWriter {
    @PersistenceContext EntityManager em;
    @Inject private HashtagDAO hashtagDAO;
    @Inject private UserDAO userDAO;

    @Override
    public void open(Serializable serializable) throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void writeItems(List<Object> list) throws Exception {
        Map<String, User> users = new HashMap<>();
        Map<String, Hashtag> tags = new HashMap<>();

        // First, create all users
        for (Object info : list) {
            String username = ((TweetProcessor.TweetProcessorInfo) info).user;
            if (!users.containsKey(username)) users.put(username, userDAO.findByUsername(username));
            if (!users.containsKey(username)) users.put(username, new User(username));
        }

        // Then, process all tweets
        for (Object info : list) {
            TweetProcessor.TweetProcessorInfo processorInfo = (TweetProcessor.TweetProcessorInfo) info;

            // Create the tweet
            Tweet tweet = new Tweet(users.get(processorInfo.user), processorInfo.tweet);
            em.persist(tweet);

            // Process the mentions
            for (String username : processorInfo.mentions) {
                if (!users.containsKey(username)) users.put(username, userDAO.findByUsername(username));
                if (users.containsKey(username)) tweet.getMentioned().add(users.get(username));
            }

            // Process the hashtags
            for (String tag : processorInfo.hashtags) {
                if (!tags.containsKey(tag)) tags.put(tag, hashtagDAO.findOrCreate(tag));
                tweet.getHashtags().add(tags.get(tag));
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
