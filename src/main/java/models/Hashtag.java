package models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

@Entity
public class Hashtag {
    static final Pattern REGEX_NAME = Pattern.compile("[A-Za-z][A-Za-z0-9_-]+[A-Za-z0-9]");

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany
    private List<Tweet> tweets = new ArrayList<>();

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
        return Collections.unmodifiableList(tweets);
    }
}
