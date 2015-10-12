package me.molchanoff.microblog.services;

import me.molchanoff.microblog.config.RootConfig;
import me.molchanoff.microblog.config.WebConfig;
import me.molchanoff.microblog.model.Role;
import me.molchanoff.microblog.model.User;
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

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class, RootConfig.class})
@ActiveProfiles("test")
@Transactional
public class UserServiceTest {
    @Autowired
    UserService userService;

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testNewUser() throws Exception {
        User user = new User();
        user.setUsername("test4");
        user.setDisplayname("test4name");
        user.setPassword("test4password");
        user.setEmail("test4email");
        user.setEnabled(true);
        userService.newUser(user, Role.ROLE_USER);
    }

    @Test
    public void testFindUser() throws Exception {
        User expectedUser = new User();
        expectedUser.setUsername("test1");
        expectedUser.setDisplayname("test1name");
        expectedUser.setEmail("test1email");
        expectedUser.setPassword("test1password");
        expectedUser.setEnabled(true);
        User actualUser = userService.findUser("test1");
        assertEquals(expectedUser, actualUser);
    }

    @Test
    public void testFindAll() throws Exception {
        List<User> userList = userService.findAll();
        User test1User = userService.findUser("test1");
        User test2User = userService.findUser("test2");
        User test3User = userService.findUser("test3");

        User testNonExistingUser = new User();
        testNonExistingUser.setUsername("test4");
        testNonExistingUser.setDisplayname("test4name");
        testNonExistingUser.setEmail("test4email");
        testNonExistingUser.setPassword("test4password");
        testNonExistingUser.setEnabled(true);

        assertTrue(userList.contains(test1User));
        assertTrue(userList.contains(test2User));
        assertTrue(userList.contains(test3User));
        assertEquals(3, userList.size());
        assertFalse(userList.contains(testNonExistingUser));
    }

    @Test
    public void testFollow() throws Exception {
        User test1 = userService.findUser("test1");
        User test2 = userService.findUser("test2");
        User test3 = userService.findUser("test3");
        assertFalse(test2.getSubscriptionList().contains(test3));
        userService.follow(test2.getUsername(), test3.getUsername());
        assertTrue(test2.getSubscriptionList().contains(test3));

        expectedException.expect(UnsupportedOperationException.class);
        userService.follow(test1.getUsername(), test2.getUsername());

    }

    @Test
    public void testUnfollow() throws Exception {
        User test1 = userService.findUser("test1");
        User test2 = userService.findUser("test2");
        User test3 = userService.findUser("test3");
        assertTrue(test1.getSubscriptionList().contains(test2));
        userService.unfollow(test1.getUsername(), test2.getUsername());
        assertFalse(test1.getSubscriptionList().contains(test2));

        expectedException.expect(UnsupportedOperationException.class);
        userService.unfollow(test2.getUsername(), test3.getUsername());
    }

    @Test
    public void testFindFollowers() throws Exception {
        User test1 = userService.findUser("test1");
        List<User> userList = userService.findFollowers("test2");
        assertTrue(userList.contains(test1));
        assertEquals(1, userList.size());
        assertNull(userService.findFollowers("test3"));
    }
}