package me.molchanoff.microblog.repositories;

import me.molchanoff.microblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    public User findByUsername(String name);
}
