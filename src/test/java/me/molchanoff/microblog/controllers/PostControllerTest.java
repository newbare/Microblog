package me.molchanoff.microblog.controllers;

import me.molchanoff.microblog.config.RootConfig;
import me.molchanoff.microblog.config.WebConfig;
import me.molchanoff.microblog.model.Post;
import me.molchanoff.microblog.model.User;
import me.molchanoff.microblog.services.PostService;
import me.molchanoff.microblog.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, RootConfig.class})
@ActiveProfiles("test")
public class PostControllerTest {
    private PostService postServiceMock;
    private UserService userServiceMock;
    private PostController controller;
    private MockMvc mockMvc;
    private TestingAuthenticationToken token;

    @Before
    public void setUp() throws Exception {
        postServiceMock = mock(PostService.class);
        userServiceMock = mock(UserService.class);
        controller = new PostController(postServiceMock, userServiceMock);
        mockMvc = standaloneSetup(controller).build();
        token = new TestingAuthenticationToken(new org.springframework.security.core.userdetails.User("test", "test", AuthorityUtils.createAuthorityList("ROLE_USER")), null);
    }

    @Test
    public void testShowPost() throws Exception {
        Post post = new Post();
        post.setMessage("Test message");
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        User user = new User();
        user.setUsername("test");
        when(postServiceMock.findPost(123)).thenReturn(post);
        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);

        mockMvc.perform(get("/post/show/123")).andExpect(view().name("post"))
                .andExpect(model().attribute("postList", postList))
                .andExpect(model().attribute("user", new User()));
        mockMvc.perform(get("/post/show/123").principal(token)).andExpect(view().name("post"))
                .andExpect(model().attribute("postList", postList))
                .andExpect(model().attribute("user", user));

        verify(postServiceMock, times(2)).findPost(123);
        verify(userServiceMock, times(1)).findUser(user.getUsername());
    }

    @Test
    public void testShowNewPage() throws Exception {
        mockMvc.perform(get("/post/new")).andExpect(view().name("newpost"));
    }

    @Test
    public void testNewPost() throws Exception {
        User user = new User();
        user.setUsername("test");
        Post post = new Post();
        post.setUser(user);
        post.setMessage("test message");
        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);

        mockMvc.perform(post("/post/new").principal(token).param("message", post.getMessage())).andExpect(redirectedUrl("/"))
                .andExpect(model().attribute("post", post));
        mockMvc.perform(post("/post/new").principal(token).param("message", "")).andExpect(view().name("newpost"))
                .andExpect(model().attributeHasFieldErrors("post", "message"));

        verify(userServiceMock, times(1)).findUser("test");
        verify(postServiceMock, times(1)).newPost(anyObject());
    }

    @Test
    public void testVotePost() throws Exception {
        mockMvc.perform(get("/post/vote").param("postId", "123").principal(token)).andExpect(redirectedUrl("/"));
        verify(postServiceMock, times(1)).votePost(123, "test");
    }

    @Test
    public void testUnvotePost() throws Exception {
        mockMvc.perform(get("/post/unvote").param("postId", "123").principal(token)).andExpect(redirectedUrl("/"));
        verify(postServiceMock, times(1)).unvotePost(123, "test");
    }
}