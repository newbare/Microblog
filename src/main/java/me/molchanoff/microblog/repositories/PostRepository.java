package me.molchanoff.microblog.repositories;

import me.molchanoff.microblog.model.Post;
import me.molchanoff.microblog.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p from Post p WHERE p.user IN :users ORDER BY p.timeStamp DESC")
    public List<Post> find(@Param("users") Set<User> followingList);

}
