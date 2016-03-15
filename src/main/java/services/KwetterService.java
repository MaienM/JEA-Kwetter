package services;

import database.daos.HashtagDAO;
import database.daos.TweetDAO;
import database.daos.UserDAO;
import database.models.Hashtag;
import database.models.Tweet;
import database.models.User;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Named
public class KwetterService {
    @Inject private HashtagDAO hashtagDAO;
    @Inject private TweetDAO tweetDAO;
    @Inject private UserDAO userDAO;

    static final Pattern REGEX_MENTION = Pattern.compile("@(?<username>" + User.REGEX_USERNAME + ")");
    static final Pattern REGEX_HASHTAG = Pattern.compile("#(?<hashtag>" + Hashtag.REGEX_NAME + ")");

    public User createUser(String username) {
        User user = new User(username);
        userDAO.save(user);
        return user;
    }

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
            hashtags.add(hashtagDAO.findOrCreate(hashtagMatcher.group("hashtag")));
        }
        tweet.setHashtags(hashtags);

        return tweet;
    }
}
