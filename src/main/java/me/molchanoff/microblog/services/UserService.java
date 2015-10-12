package me.molchanoff.microblog.services;

import me.molchanoff.microblog.model.Authority;
import me.molchanoff.microblog.model.Role;
import me.molchanoff.microblog.model.User;
import me.molchanoff.microblog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * This class provides methods to process users
 */
@Service
@Transactional
public class UserService {
    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * This method registers new user
     * @param user User object
     * @param role Role enumeration which describes user authority
     */
    public void newUser(User user, Role role) {
        if (userRepository.findByUsername(user.getUsername()) != null) throw new UnsupportedOperationException("User already registered");
        Authority auth = new Authority();
        auth.setRole(role);
        auth.setUser(user);
        user.getAuthorityList().add(auth);
        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * This method returns a user by its username
     * @param username username
     * @return User object
     */
    public User findUser(String username) {
        User user = userRepository.findByUsername(username);
        return user;
    }

    /**
     * This method fetches all user in ascending order by their username
     * @return
     */
    public List<User> findAll() {
        return userRepository.findAll(new Sort(Sort.Direction.ASC, "username"));
    }

    /**
     * This method is used to follow user
     * @param followerUsername the name of user who wants to follow
     * @param followedUsername the name of user who will be followed
     */
    public void follow(String followerUsername, String followedUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followed = userRepository.findByUsername(followedUsername);
        if (followed.equals(follower)) throw new UnsupportedOperationException("User cannot follow himself");
        else if (follower.getSubscriptionList().contains(followed)) throw new UnsupportedOperationException("User already followed");
        else {
            follower.getSubscriptionList().add(followed);
            userRepository.save(follower);
        }
    }

    /**
     * This method is used to unfollow user
     * @param followerUsername the name of user who wants to unfollow
     * @param followedUsername the name of user who will not be followed
     */
    public void unfollow(String followerUsername, String followedUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        User followed = userRepository.findByUsername(followedUsername);
        if (followed.equals(follower)) throw new UnsupportedOperationException("User cannot unfollow himself");
        else if (!follower.getSubscriptionList().contains(followed)) throw new UnsupportedOperationException("User was not followed");
        else {
            follower.getSubscriptionList().remove(followed);
            userRepository.save(follower);
        }
    }

    /**
     * This method is used to retrieve all users who follow specified user
     * @param username the name of followed user
     * @return users who follow specific user
     */
    public List<User> findFollowers(String username) {
        User user = userRepository.findByUsername(username);
        List<User> userList = findAll();
        List<User> followersList = new ArrayList<>();
        for (User x : userList) {
            if (x.getSubscriptionList().contains(user)) followersList.add(x);
        }
        if (!followersList.isEmpty()) return followersList;
        else return null;
    }

}
