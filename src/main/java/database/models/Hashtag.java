package database.models;

import flexjson.JSON;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Entity
@Table(name = "hashtags")
public class Hashtag {
    public static final Pattern REGEX_NAME = Pattern.compile("[A-Za-z][A-Za-z0-9_-]+[A-Za-z0-9]");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @JSON(include = false)
    @ManyToMany(mappedBy = "hashtags", cascade = CascadeType.ALL)
    private Set<Tweet> tweets = new HashSet<>();

    public Hashtag() {}
    public Hashtag(String name) {
        this.name = name.toLowerCase();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(Set<Tweet> tweets) {
        this.tweets = tweets;
    }
}
