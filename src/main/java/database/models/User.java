package database.models;

import services.KwetterService;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Entity
public class User {
    public static final Pattern REGEX_USERNAME = Pattern.compile("[A-Za-z][A-Za-z0-9_-]*");

    @Inject
    private KwetterService service;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String username;

    @OneToMany
    private List<Tweet> tweets = new ArrayList<>();

    @ManyToMany
    private List<Tweet> mentions = new ArrayList<>();

    @ManyToMany(mappedBy = "follower")
    private List<User> following = new ArrayList<>();

    @ManyToMany(mappedBy = "followee")
    private List<User> followers = new ArrayList<>();

    public User() {}
    public User(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }

    private void addTweet(Tweet tweet) {
        this.tweets.add(tweet);
    }

    public Tweet addTweet(String content) {
        Tweet tweet = new Tweet(this, content);
        addTweet(tweet);
        return tweet;
    }

    public List<Tweet> getMentions() {
        return mentions;
    }

    public void setMentions(List<Tweet> mentions) {
        this.mentions = mentions;
    }

    public List<User> getFollowing() {
        return following;
    }

    public void setFollowing(List<User> following) {
        this.following = following;
    }

    public List<User> getFollowers() {
        return followers;
    }

    public void setFollowers(List<User> followers) {
        this.followers = followers;
    }
}

