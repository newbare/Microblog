package me.molchanoff.microblog.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.*;

/**
 * Object which is used to represent user
 */
@Entity
@Table(name = "USERS")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    @Size(min = 1, max = 20)
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "DISPLAYNAME", nullable = false)
    private String displayname;

    @NotNull
    @Size(min = 1, max = 40)
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "ENABLED", nullable = false)
    private boolean enabled;

    @OneToMany(targetEntity = Post.class, fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Post> postList = new ArrayList<>();

    @OneToMany(targetEntity = Authority.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private Set<Authority> authorityList = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "SUBSCRIPTIONS", joinColumns = {@JoinColumn(name = "OWNER_ID", referencedColumnName = "ID")}, inverseJoinColumns = {@JoinColumn(name = "FOLLOWED_ID", referencedColumnName = "ID")})
    private Set<User> subscriptionList = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "votersList")
    private Set<Post> votedPostsList = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayname() {
        return displayname;
    }

    public void setDisplayname(String displayname) {
        this.displayname = displayname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<Post> getPostList() {
        return postList;
    }

    public void setPostList(List<Post> postList) {
        this.postList = postList;
    }

    public Set<Authority> getAuthorityList() {
        return authorityList;
    }

    public void setAuthorityList(Set<Authority> authorityList) {
        this.authorityList = authorityList;
    }

    public Set<User> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(Set<User> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }

    public Set<Post> getVotedPostsList() {
        return votedPostsList;
    }

    public void setVotedPostsList(Set<Post> votedPostsList) {
        this.votedPostsList = votedPostsList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(enabled, user.enabled) &&
                Objects.equals(username, user.username) &&
                Objects.equals(displayname, user.displayname) &&
                Objects.equals(email, user.email) &&
                Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, displayname, email, password, enabled);
    }
}
