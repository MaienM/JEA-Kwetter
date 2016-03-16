package database.models;

import services.KwetterService;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Entity
@Table(name = "users")
public class User {
    public static final Pattern REGEX_USERNAME = Pattern.compile("[A-Za-z][A-Za-z0-9_-]*");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String username;

    @OneToMany(mappedBy = "user")
    private List<Tweet> tweets = new ArrayList<>();

    @ManyToMany(mappedBy = "mentioned")
    private List<Tweet> mentions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "followee_id", referencedColumnName = "id")
    )
    private List<User> following = new ArrayList<>();

    @ManyToMany(mappedBy = "following")
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

