package me.molchanoff.microblog.services;

import me.molchanoff.microblog.model.Post;
import me.molchanoff.microblog.model.User;
import me.molchanoff.microblog.repositories.PostRepository;
import me.molchanoff.microblog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * This class provides methods to process posts
 */
@Service
@Transactional
public class PostService {
    private UserRepository userRepository;
    private PostRepository postRepository;

    @Autowired
    public PostService(UserRepository userRepository, PostRepository postRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
     * This method returns all posts sorted in descending order by timestamp.
     * @return list of all posts
     * @see Post
     */
    public List<Post> findAll() {
        return postRepository.findAll(new Sort(Sort.Direction.DESC, "timeStamp"));
    }

    /**
     * This method is used to retrieve all posts of users the follower subscribed
     * @param followerUsername follower username
     * @return list of posts
     */
    public List<Post> findFollowing(String followerUsername) {
        User follower = userRepository.findByUsername(followerUsername);
        if (!follower.getSubscriptionList().isEmpty()) {
            return postRepository.find(follower.getSubscriptionList());
        }
        else return null;
    }

    /**
     * This method creates new Post
     * @param post new post
     */
    public void newPost(Post post) {
        post.setTimeStamp(new Date());
        postRepository.save(post);
    }

    /**
     * This method is used to fetch a post by its ID
     * @param id post ID
     * @return specified post
     */
    public Post findPost(long id) {
        return postRepository.findOne(id);
    }

    /**
     * This method retrieves all post of specified user
     * @param username username
     * @return posts of specified user
     */
    public List<Post> findByUsername(String username) {
        User user = userRepository.findByUsername(username);
        user.getPostList().size();
        return user.getPostList();
    }

    /**
     * This method is used to vote for post
     * @param postId post ID user wants to vote for
     * @param voterUsername voter username
     */
    public void votePost(long postId, String voterUsername) {
        Post post = postRepository.findOne(postId);
        User voter = userRepository.findByUsername(voterUsername);
        if (post.getUser().equals(voter)) throw new UnsupportedOperationException("You cannot vote your own post");
        else if (post.getVotersList().contains(voter) && voter.getVotedPostsList().contains(post)) throw new UnsupportedOperationException("You already voted for this post");
        else {
            post.getVotersList().add(voter);
            voter.getVotedPostsList().add(post);
            postRepository.save(post);
            userRepository.save(voter);
        }
    }

    /**
     * This method is used to unvote post
     * @param postId post ID user wants to unvote
     * @param voterUsername voter username
     */
    public void unvotePost(long postId, String voterUsername) {
        Post post = postRepository.findOne(postId);
        User voter = userRepository.findByUsername(voterUsername);
        if (post.getUser().equals(voter)) throw new UnsupportedOperationException("You cannot unvote your own post");
        else if (!post.getVotersList().contains(voter) && !voter.getVotedPostsList().contains(post)) throw new UnsupportedOperationException("Post wasn't voted");
        else {
            post.getVotersList().remove(voter);
            voter.getVotedPostsList().remove(post);
            postRepository.save(post);
            userRepository.save(voter);
        }
    }

}
