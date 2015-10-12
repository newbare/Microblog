package me.molchanoff.microblog.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Authority object which describes user's role
 */
@Entity
@Table(name = "AUTHORITIES")
public class Authority implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;

    @Column(name = "ROLE", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Authority authority = (Authority) o;
        return Objects.equals(user, authority.user) &&
                Objects.equals(role, authority.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, role);
    }
}
