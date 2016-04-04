package batch;

import database.daos.HashtagDAO;
import database.daos.TweetDAO;
import database.daos.UserDAO;
import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;

import javax.batch.api.chunk.ItemWriter;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Dependent
@Named
public class TweetWriter implements ItemWriter {
    @Inject private HashtagDAO hashtagDAO;
    @Inject private TweetDAO tweetDAO;
    @Inject private UserDAO userDAO;

    @Override
    public void open(Serializable serializable) throws Exception {
    }

    @Override
    public void close() throws Exception {
    }

    @Override
    public void writeItems(List<Object> list) throws Exception {
        Logger logger = Logger.getLogger(getClass().getName());
        logger.warning("Writing");

        Map<String, User> users = new HashMap<>();
        Map<String, Hashtag> tags = new HashMap<>();

        // First, create all users
        for (Object info : list) {
            String username = ((TweetProcessor.TweetProcessorInfo) info).user;
            if (!users.containsKey(username)) users.put(username, userDAO.findByUsername(username));
            if (users.get(username) == null) users.put(username, new User(username));
        }

        // Then, process all tweets
        for (Object info : list) {
            TweetProcessor.TweetProcessorInfo processorInfo = (TweetProcessor.TweetProcessorInfo) info;

            // Create the tweet
            Tweet tweet = new Tweet(users.get(processorInfo.user), processorInfo.tweet);
            tweetDAO.save(tweet);

            // Process the mentions
            for (String username : processorInfo.mentions) {
                if (!users.containsKey(username)) users.put(username, userDAO.findByUsername(username));
                if (users.get(username) != null) tweet.getMentioned().add(users.get(username));
            }

            // Process the hashtags
            for (String tag : processorInfo.hashtags) {
                if (!tags.containsKey(tag)) tags.put(tag, hashtagDAO.findOrCreateByName(tag));
                tweet.getHashtags().add(tags.get(tag));
            }

            // FIXME:
            // Automatically follow all users that you mention so that I have test data
            for (User mentioned : tweet.getMentioned()) {
                users.get(processorInfo.user).getFollowing().add(mentioned);
            }
        }
    }

    @Override
    public Serializable checkpointInfo() throws Exception {
        return null;
    }
}
