package me.molchanoff.microblog.services;

import me.molchanoff.microblog.model.Authority;
import me.molchanoff.microblog.model.Role;
import me.molchanoff.microblog.model.User;
import me.molchanoff.microblog.repositories.UserRepository;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import javax.servlet.http.Part;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This class provides methods to process users
 */
@Service
@Transactional
public class UserService {
    private UserRepository userRepository;

    @Autowired
    ServletContext context;

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
        user.setUserPicturePrefix("default");
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

    /**
     * This method is used to upload user picture. The files are located in userpics catalog
     * in servlet context directory.
     * Generated UUID is used as filename prefix. After  method ensures that UUID is unique,
     * two files are saved with generated UUID as prefix
     * and "_large" and "_small" postfix and UUID is added to database to the user entry.
     * @param userPicture uploaded user picture
     * @param userId user ID
     * @throws IOException
     */
    public void uploadPicture(Part userPicture, long userId) throws IOException {
        BufferedImage image = ImageIO.read(userPicture.getInputStream());
        BufferedImage resizedLarge = Scalr.resize(image, Scalr.Method.QUALITY, 250, Scalr.OP_ANTIALIAS);
        BufferedImage resizedSmall = Scalr.resize(image, Scalr.Method.QUALITY, 64, Scalr.OP_ANTIALIAS);
        String filenamePrefix = UUID.randomUUID().toString();
        File outputLarge = new File(context.getRealPath("/userpics/") + filenamePrefix + "_large.png");
        File outputSmall = new File(context.getRealPath("/userpics/") + filenamePrefix + "_small.png");
        while (outputLarge.exists() || outputSmall.exists()) {
            filenamePrefix = UUID.randomUUID().toString();
            outputLarge = new File(context.getRealPath("/userpics/") + filenamePrefix + "_large.png");
            outputSmall = new File(context.getRealPath("/userpics/") + filenamePrefix + "_small.png");
        }
        ImageIO.write(resizedLarge, "png", outputLarge);
        ImageIO.write(resizedSmall, "png", outputSmall);
        User user = userRepository.findOne(userId);
        user.setUserPicturePrefix(filenamePrefix);
        userRepository.save(user);
    }

    /**
     * This method removes user picture from disk and sets user_picture_prefix field to "default"
     * indicating that default user picture is used.
     * @param userId user ID
     */
    public void removePicture(long userId) {
        User user = userRepository.findOne(userId);
        File pictureLarge = new File(context.getRealPath("/userpics/") + user.getUserPicturePrefix() + "_large.png");
        File pictureSmall = new File(context.getRealPath("/userpics/") + user.getUserPicturePrefix() + "_small.png");
        pictureLarge.delete();
        pictureSmall.delete();
        user.setUserPicturePrefix("default");
        userRepository.save(user);
    }

}
