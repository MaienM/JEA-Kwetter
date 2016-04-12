package database.models;

import flexjson.JSON;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Entity
@Table(name = "groups")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private String name;

    @JSON(include = false)
    @ManyToMany(mappedBy = "groups", cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();

    public Group() {}
    public Group(String name) {
        this.name = name.toLowerCase();
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
}
