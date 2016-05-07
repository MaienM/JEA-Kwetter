package database.models;

import flexjson.JSON;
import services.KwetterService;

import javax.inject.Inject;
import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
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

    @JSON(include = false)
    @Column(nullable = false)
    private String password;

    @JSON(include = false)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<Tweet> tweets = new HashSet<>();

    @JSON(include = false)
    @ManyToMany(mappedBy = "mentioned", cascade = CascadeType.ALL)
    private Set<Tweet> mentions = new HashSet<>();

    @JSON(include = false)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "follows",
            joinColumns = @JoinColumn(name = "follower_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "followee_id", referencedColumnName = "id")
    )
    private Set<User> following = new HashSet<>();

    @JSON(include = false)
    @ManyToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private Set<User> followers = new HashSet<>();

    @JSON(include = false)
    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Group> groups = new HashSet<>();

    public User() {}
    public User(String username) {
        this(username, "test");
    }
    public User(String username, String password) {
        this.username = username;
        this.setPassword(password);
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

    public void setPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes("UTF-8"));
            byte[] digest = md.digest();
            BigInteger bigInt = new BigInteger(1, digest);
            this.password = bigInt.toString(16);
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Set<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(Set<Tweet> tweets) {
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

    public Set<Tweet> getMentions() {
        return mentions;
    }

    public void setMentions(Set<Tweet> mentions) {
        this.mentions = mentions;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups = groups;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof User) {
            return getId() == ((User) other).getId();
        }
        return false;
    }
}

