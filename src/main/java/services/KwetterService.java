package services;

import database.daos.HashtagDAO;
import database.daos.TweetDAO;
import database.daos.UserDAO;
import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
@Stateless
public class KwetterService {
    @Inject private HashtagDAO hashtagDAO;
    @Inject private TweetDAO tweetDAO;
    @Inject private UserDAO userDAO;

    public static final Pattern REGEX_MENTION = Pattern.compile("@(?<username>" + User.REGEX_USERNAME + ")");
    public static final Pattern REGEX_HASHTAG = Pattern.compile("#(?<hashtag>" + Hashtag.REGEX_NAME + ")");

    /**
     * Create an user
     *
     * @param username The username of the new user
     * @return The new user
     */
    public User createUser(String username) {
        User user = new User(username);
        userDAO.save(user);
        return user;
    }

    /**
     * Get the total number of tweets
     *
     * @return The tweet count
     */
    public long getTweetCount() {
        return tweetDAO.getCount();
    }

    /**
     * Get the 50 miost recent tweets
     *
     * @return The tweets
     */
    public List<Tweet> getRecentTweets() {
        return tweetDAO.getRecent();
    }

    /**
     * Get the 50 most recent tweets with a given hashtag
     *
     * @param tag The hashtag, without the # prefix
     * @return The tweets
     */
    public List<Tweet> getRecentTweetsHashtag(String tag) {
        return tweetDAO.getRecentByHashtag(hashtagDAO.findByName(tag));
    }

    /**
     * Get the 50 most recent tweets from users that the given user follows
     *
     * @param user The user whoms follows to use
     * @return The tweets
     */
    public List<Tweet> getRecentTweetsFollows(User user) {
        return tweetDAO.getRecentOfFollows(user);
    }

    /**
     * Get the 50 most recent tweets mentioning the given user
     *
     * @param user The user that should be mentioned
     * @return The tweets
     */
    public List<Tweet> getRecentTweetsMentioning(User user) {
        return tweetDAO.getRecentByMentioning(user);
    }

    /**
     * Get the 50 most recent tweets containing the given text
     *
     * @param text The text to search for
     * @return The tweets
     */
    public List<Tweet> findTweetsByText(String text) {
        return tweetDAO.findByText(text);
    }

    /**
     * Create a tweet
     *
     * @param user The user that posted the tweet
     * @param content The text of the tweet
     * @return The new tweet
     */
    public Tweet createTweet(User user, String content) {
        Tweet tweet = user.addTweet(content);

        // Find mentions.
        List<User> mentioned = new ArrayList<>();
        Matcher mentionMatcher = REGEX_MENTION.matcher(content);
        while (mentionMatcher.find()) {
            User mention = userDAO.findByUsername(mentionMatcher.group("username"));
            if (mention == null) continue;
            mentioned.add(mention);
        }
        tweet.setMentioned(mentioned);

        // Find hashtags.
        List<Hashtag> hashtags = new ArrayList<>();
        Matcher hashtagMatcher = REGEX_HASHTAG.matcher(content);
        while (hashtagMatcher.find()) {
            hashtags.add(hashtagDAO.findOrCreateByName(hashtagMatcher.group("hashtag")));
        }
        tweet.setHashtags(hashtags);

        tweetDAO.save(tweet);
        return tweet;
    }

    /**
     * Get the followers that the given follower is following
     *
     * @param follower The follower whose follows to get
     * @return The followers that follower is following
     */
    public List<User> getFollows(User follower) {
        return follower.getFollowing();
    }

    /**
     * Add a follow to the given follower
     *
     * @param follower The follower whose follows to modify
     * @param followee The follower to add to the follows list
     */
    public void addFollow(User follower, User followee) {
        follower.getFollowing().add(followee);
        userDAO.save(follower);
    }

    /**
     * Remove a follow from the given follower
     *
     * @param follower The follower whose follows to modify
     * @param followee The follower to remove from the follows list
     */
    public void removeFollow(User follower, User followee) {
        follower.getFollowing().remove(followee);
        userDAO.save(follower);
    }
}
