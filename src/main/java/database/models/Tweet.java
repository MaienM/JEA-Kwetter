package database.models;

import flexjson.JSON;
import services.KwetterService;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tweets")
public class Tweet implements Comparable<Tweet> {
    @Transient
    public final int MAX_LENGTH = 140;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @JSON(include = false)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "mentions",
            joinColumns = @JoinColumn(name = "tweet_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private Set<User> mentioned = new HashSet<>();

    @JSON(include = false)
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "tweet_tags",
            joinColumns = @JoinColumn(name = "tweet_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id", referencedColumnName = "id")
    )
    private Set<Hashtag> hashtags = new HashSet<>();

    public Tweet() {}
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

    public void setContent(String content) {
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException("Tweet content is empty");
        }
        if (content.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(String.format("Tweet content exceeds %d characters", MAX_LENGTH));
        }
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<User> getMentioned() {
        return mentioned;
    }

    public void setMentioned(Set<User> mentioned) {
        this.mentioned = mentioned;
    }

    public Set<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(Set<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }

    @Override
    public int compareTo(Tweet o) {
        return Long.compare(id, o.id);
    }
}
