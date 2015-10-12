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
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, RootConfig.class})
@ActiveProfiles("test")
public class HomeControllerTest {
    private PostService postServiceMock;
    private UserService userServiceMock;
    private HomeController controller;
    private MockMvc mockMvc;
    private TestingAuthenticationToken token;

    @Before
    public void setUp() throws Exception {
        postServiceMock = mock(PostService.class);
        userServiceMock = mock(UserService.class);
        controller = new HomeController(postServiceMock, userServiceMock);
        token = new TestingAuthenticationToken(new org.springframework.security.core.userdetails.User("test", "test", AuthorityUtils.createAuthorityList("ROLE_USER")), null);
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".html");
        mockMvc = standaloneSetup(controller).setViewResolvers(resolver).build();
    }

    @Test
    public void testShowHome() throws Exception {
        mockMvc.perform(get("/")).andExpect(redirectedUrl("/recent"));
    }

    @Test
    public void testShowRecent() throws Exception {
        Post post = new Post();
        post.setMessage("test message");
        User user = new User();
        user.setUsername("test");
        List<Post> postList = new ArrayList<>();
        postList.add(post);

        when(postServiceMock.findAll()).thenReturn(postList);
        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);

        mockMvc.perform(get("/recent")).andExpect(view().name("recent"))
                .andExpect(model().attribute("user", new User()))
                .andExpect(model().attribute("postList", postList));
        mockMvc.perform(get("/recent").principal(token)).andExpect(view().name("recent"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("postList", postList));

        verify(postServiceMock, times(2)).findAll();
        verify(userServiceMock, times(1)).findUser(anyObject());
    }

    @Test
    public void testShowMyPosts() throws Exception {
        Post post = new Post();
        post.setMessage("test message");
        User user = new User();
        user.setUsername("test");
        List<Post> postList = new ArrayList<>();
        postList.add(post);

        when(postServiceMock.findByUsername(user.getUsername())).thenReturn(postList);
        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);

        mockMvc.perform(get("/myposts").principal(token)).andExpect(view().name("myposts"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("postList", postList));

        verify(postServiceMock, times(1)).findByUsername(user.getUsername());
        verify(userServiceMock, times(1)).findUser(user.getUsername());
    }

    @Test
    public void testShowFeed() throws Exception {
        Post post = new Post();
        post.setMessage("test message");
        User user = new User();
        user.setUsername("test");
        List<Post> postList = new ArrayList<>();
        postList.add(post);

        when(postServiceMock.findFollowing(user.getUsername())).thenReturn(postList);
        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);

        mockMvc.perform(get("/feed").principal(token)).andExpect(view().name("feed"))
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attributeExists("postList"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("postList", postList));

        verify(postServiceMock, times(1)).findFollowing(user.getUsername());
        verify(userServiceMock, times(1)).findUser(user.getUsername());
    }

    @Test
    public void testShowProfile() throws Exception {
        Post post = new Post();
        post.setMessage("test message");
        User user = new User();
        user.setUsername("test");
        User follower1 = new User();
        follower1.setUsername("follower1");
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        List<User> followersList = new ArrayList<>();
        followersList.add(follower1);

        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);
        when(userServiceMock.findFollowers(user.getUsername())).thenReturn(followersList);
        when(postServiceMock.findByUsername(user.getUsername())).thenReturn(postList);

        mockMvc.perform(get("/profile").principal(token)).andExpect(view().name("profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("postList", postList))
                .andExpect(model().attribute("followersList", followersList));

        verify(userServiceMock, times(1)).findUser(user.getUsername());
        verify(userServiceMock, times(1)).findFollowers(user.getUsername());
        verify(postServiceMock, times(1)).findByUsername(user.getUsername());
    }

    @Test
    public void testShowLoginPage() throws Exception {
        mockMvc.perform(get("/login")).andExpect(view().name("login"));
        mockMvc.perform(get("/login").param("error", "")).andExpect(view().name("login"))
                .andExpect(model().attributeExists("alert"));
    }
}