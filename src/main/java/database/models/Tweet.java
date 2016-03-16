package database.models;

import services.KwetterService;

import javax.inject.Inject;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tweets")
public class Tweet {
    @Transient
    public final int MAX_LENGTH = 140;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany
    @JoinTable(
            name = "mentions",
            joinColumns = @JoinColumn(name = "tweet_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id")
    )
    private List<User> mentioned = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "tweet_tags",
            joinColumns = @JoinColumn(name = "tweet_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "hashtag_id", referencedColumnName = "id")
    )
    private List<Hashtag> hashtags = new ArrayList<>();

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

    public List<User> getMentioned() {
        return mentioned;
    }

    public void setMentioned(List<User> mentioned) {
        this.mentioned = mentioned;
    }

    public List<Hashtag> getHashtags() {
        return hashtags;
    }

    public void setHashtags(List<Hashtag> hashtags) {
        this.hashtags = hashtags;
    }
}
