package me.molchanoff.microblog.services;

import me.molchanoff.microblog.config.RootConfig;
import me.molchanoff.microblog.config.WebConfig;
import me.molchanoff.microblog.model.Post;
import me.molchanoff.microblog.model.User;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, RootConfig.class})
@ActiveProfiles("test")
@Transactional
public class PostServiceTest {
    @Autowired
    PostService postService;

    @Autowired
    UserService userService;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testFindAll() throws Exception {
        List<Post> postListExpected = new ArrayList<>();
        for (int i = 1; i <= 9; i++) postListExpected.add(postService.findPost(i));
        List<Post> postListActual = postService.findAll();
        assertTrue(postListActual.containsAll(postListExpected));
        assertTrue(postListExpected.containsAll(postListActual));
    }

    @Test
    public void testFindFollowing() throws Exception {
        List<Post> feed = postService.findFollowing("test1");
        List<Post> feedExpected = postService.findByUsername("test2");
        assertTrue(feed.containsAll(feedExpected));
        assertTrue(feedExpected.containsAll(feed));
        assertNull(postService.findFollowing("test3"));
    }

    @Test
    public void testNewPost() throws Exception {
        User user = userService.findUser("test1");
        Post post = new Post();
        post.setUser(user);
        post.setMessage("Test message");
        post.setTimeStamp(new Date());
        postService.newPost(post);
        List<Post> postList = postService.findAll();
        assertTrue(postList.contains(post));
    }

    @Test
    public void testFindPost() throws Exception {
        User user = userService.findUser("test1");
        Post expectedPost = new Post();
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        expectedPost.setMessage("first message from test1");
        expectedPost.setTimeStamp(formatter.parse("2015-01-01 00:00:00"));
        expectedPost.setUser(user);
        Post actualPost = postService.findPost(1);
        assertEquals(expectedPost, actualPost);
    }

    @Test
    public void testFindByUsername() throws Exception {
        List<Post> expectedPostList = new ArrayList<>();
        for (int i = 3; i >= 1; i--) expectedPostList.add(postService.findPost(i));
        List<Post> actualPostList = postService.findByUsername("test1");
        assertEquals(expectedPostList, actualPostList);
    }

    @Test
    public void testVotePost() throws Exception {
        Post post = postService.findPost(5);
        User voter = userService.findUser("test1");
        assertFalse(post.getVotersList().contains(voter));
        postService.votePost(5, "test1");
        assertTrue(post.getVotersList().contains(voter));

        expectedException.expect(UnsupportedOperationException.class);
        postService.votePost(1, "test1");
        postService.votePost(4, "test1");
    }

    @Test
    public void testUnvotePost() throws Exception {
        Post post = postService.findPost(4);
        User voter = userService.findUser("test1");
        assertTrue(post.getVotersList().contains(voter));
        postService.unvotePost(4, "test1");
        assertFalse(post.getVotersList().contains(voter));

        expectedException.expect(UnsupportedOperationException.class);
        postService.unvotePost(1, "test1");
        postService.unvotePost(4, "test1");
    }
}