package database.models;

import services.KwetterService;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Entity
public class Hashtag {
    public static final Pattern REGEX_NAME = Pattern.compile("[A-Za-z][A-Za-z0-9_-]+[A-Za-z0-9]");

    @Inject
    private KwetterService service;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    private List<Tweet> tweets = new ArrayList<>();

    public Hashtag() {}
    public Hashtag(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Tweet> getTweets() {
        return tweets;
    }

    public void setTweets(List<Tweet> tweets) {
        this.tweets = tweets;
    }
}
