package me.molchanoff.microblog.controllers;

import me.molchanoff.microblog.config.RootConfig;
import me.molchanoff.microblog.config.WebConfig;
import me.molchanoff.microblog.model.Post;
import me.molchanoff.microblog.model.Role;
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
public class UserControllerTest {
    private UserService userServiceMock;
    private PostService postServiceMock;
    private UserController controller;
    private MockMvc mockMvc;
    private TestingAuthenticationToken token;

    @Before
    public void setUp() throws Exception {
        postServiceMock = mock(PostService.class);
        userServiceMock = mock(UserService.class);
        controller = new UserController(postServiceMock, userServiceMock);
        token = new TestingAuthenticationToken(new org.springframework.security.core.userdetails.User("test", "test", AuthorityUtils.createAuthorityList("ROLE_USER")), null);
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/views/");
        resolver.setSuffix(".html");
        mockMvc = standaloneSetup(controller).setViewResolvers(resolver).build();
    }

    @Test
    public void testShowRegisterPage() throws Exception {
        mockMvc.perform(get("/user/signup")).andExpect(view().name("signup"));
    }

    @Test
    public void testRegisterUser() throws Exception {
        User user = new User();
        user.setUsername("test");
        user.setPassword("test");
        user.setDisplayname("Test user");
        user.setEmail("test@domain.com");

        mockMvc.perform(post("/user/signup").param("username", user.getUsername())
                .param("displayname", user.getDisplayname())
                .param("password", user.getPassword())
                .param("email", user.getEmail())).andExpect(redirectedUrl("/"))
                .andExpect(model().attribute("user", user));
        mockMvc.perform(post("/user/signup").param("username", "")
                .param("displayname", user.getDisplayname())
                .param("password", user.getPassword())
                .param("email", user.getEmail()))
                .andExpect(view().name("signup"))
                .andExpect(model().attributeHasFieldErrors("user", "username"));
        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);
        mockMvc.perform(post("/user/signup").param("username", user.getUsername())
                .param("displayname", user.getDisplayname())
                .param("password", user.getPassword())
                .param("email", user.getEmail())).andExpect(view().name("signup"))
                .andExpect(model().attributeExists("alert"));

        verify(userServiceMock, times(1)).newUser(anyObject(), eq(Role.ROLE_USER));
    }

    @Test
    public void testShowUserPage() throws Exception {
        User user = new User();
        user.setUsername("test");
        User anotherUser = new User();
        anotherUser.setUsername("anotherUser");
        Post post = new Post();
        post.setMessage("Test message");
        post.setUser(anotherUser);
        List<Post> postList = new ArrayList<>();
        postList.add(post);
        List<User> followersList = new ArrayList<>();
        followersList.add(anotherUser);

        when(userServiceMock.findUser(user.getUsername())).thenReturn(user);
        when(userServiceMock.findUser(anotherUser.getUsername())).thenReturn(anotherUser);
        when(postServiceMock.findByUsername(anotherUser.getUsername())).thenReturn(postList);
        when(userServiceMock.findFollowers(anotherUser.getUsername())).thenReturn(followersList);
        mockMvc.perform(get("/user/show/anotherUser")).andExpect(view().name("profile"))
                .andExpect(model().attribute("user", new User()))
                .andExpect(model().attribute("postList", postList))
                .andExpect(model().attribute("profileUser", anotherUser))
                .andExpect(model().attribute("followersList", followersList));
        mockMvc.perform(get("/user/show/anotherUser").principal(token)).andExpect(view().name("profile"))
                .andExpect(model().attribute("user", user))
                .andExpect(model().attribute("postList", postList))
                .andExpect(model().attribute("profileUser", anotherUser))
                .andExpect(model().attribute("followersList", followersList));

        verify(userServiceMock, times(2)).findUser(anotherUser.getUsername());
        verify(userServiceMock, times(1)).findUser(user.getUsername());
        verify(postServiceMock, times(2)).findByUsername(anotherUser.getUsername());
    }

    @Test
    public void testFollowUser() throws Exception {
        User followed = new User();
        followed.setUsername("followedUser");
        User follower = new User();
        follower.setUsername("test");
        mockMvc.perform(get("/user/follow").principal(token).param("user", followed.getUsername())).andExpect(redirectedUrl("/"));
        verify(userServiceMock, times(1)).follow(follower.getUsername(), followed.getUsername());
    }

    @Test
    public void testUnfollowUser() throws Exception {
        User followed = new User();
        followed.setUsername("followedUser");
        User follower = new User();
        follower.setUsername("test");
        mockMvc.perform(get("/user/unfollow").principal(token).param("user", followed.getUsername())).andExpect(redirectedUrl("/"));
        verify(userServiceMock, times(1)).unfollow(follower.getUsername(), followed.getUsername());
    }
}