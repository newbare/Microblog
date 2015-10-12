package me.molchanoff.microblog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Object which is used to store post
 */
@Entity
@Table(name = "POSTS")
public class Post implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @NotNull
    @Size(min = 1, max = 160)
    @Column(name = "MESSAGE", nullable = false)
    private String message;

    @Column(name = "TIMESTAMP", nullable = false)
    private Date timeStamp;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "VOTES", joinColumns = {@JoinColumn(name = "POST_ID", referencedColumnName = "ID")}, inverseJoinColumns = {@JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private Set<User> votersList = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Set<User> getVotersList() {
        return votersList;
    }

    public void setVotersList(Set<User> votersList) {
        this.votersList = votersList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(user, post.user) &&
                Objects.equals(message, post.message) &&
                Objects.equals(timeStamp, post.timeStamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, message, timeStamp);
    }
}
