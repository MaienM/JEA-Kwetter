package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Entity
public class User {
    static final Pattern REGEX_USERNAME = Pattern.compile("[A-Za-z][A-Za-z0-9_-]*");

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
        return Collections.unmodifiableList(tweets);
    }

    private Tweet addTweet(Tweet tweet) {
        tweets.add(tweet);
        return tweet;
    }

    public Tweet addTweet(String content) {
        return addTweet(new Tweet(this, content));
    }

    public List<Tweet> getMentions() {
        return Collections.unmodifiableList(mentions);
    }

    public List<User> getFollowing() {
        return Collections.unmodifiableList(following);
    }

    public User addFollowing(User user) {
        following.add(user);
        user.followers.add(this);
        return user;
    }

    public List<User> getFollowers() {
        return Collections.unmodifiableList(followers);
    }
}
