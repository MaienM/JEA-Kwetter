package models;

import com.avaje.ebean.Ebean;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Entity
public class Tweet {
    public final int MAX_LENGTH = 140;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false)
    private User user;

    @ManyToMany
    private List<User> mentioned = new ArrayList<>();

    @ManyToMany
    private List<Hashtag> hashtags = new ArrayList<>();

    public Tweet(User user, String content) {
        this.user = user;
        this.setContent(content);
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    static final Pattern REGEX_MENTION = Pattern.compile("@(?<username>" + User.REGEX_USERNAME + ")");
    static final Pattern REGEX_HASHTAG = Pattern.compile("#(?<hashtag>" + Hashtag.REGEX_NAME + ")");

    public void setContent(String content) {
        this.content = content;

        if (content.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Tweet content exceeds %d characters", MAX_LENGTH));
        }

        // Find mentions.
        mentioned.clear();
        Matcher mentionMatcher = REGEX_MENTION.matcher(content);
        while (mentionMatcher.find()) {
            // TODO: Don't create new users!.
            this.mentioned.add(new User(mentionMatcher.group("username")));
        }

        // Find mentions.
        hashtags.clear();
        Matcher hashtagMatcher = REGEX_HASHTAG.matcher(content);
        while (hashtagMatcher.find()) {
            // TODO: Only create new tags if needed.
            this.hashtags.add(new Hashtag(hashtagMatcher.group("hashtag")));
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<User> getMentioned() {
        return Collections.unmodifiableList(mentioned);
    }

    public List<Hashtag> getHashtags() {
        return Collections.unmodifiableList(hashtags);
    }
}
