package me.molchanoff.microblog.controllers;

import me.molchanoff.microblog.model.*;
import me.molchanoff.microblog.services.PostService;
import me.molchanoff.microblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    private PostService postService;
    private UserService userService;

    @Autowired
    public UserController(PostService postService, UserService userService) {
        this.userService = userService;
        this.postService = postService;
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String showRegisterPage(Model model) {
        model.addAttribute(new User());
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String registerUser(@Valid User user, Errors errors, Model model) {
        if (errors.hasErrors()) return "signup";
        if (userService.findUser(user.getUsername()) != null) {
            Alert alert = new Alert(AlertType.DANGER, "Error!", "User with name " + user.getUsername() + " already registered");
            model.addAttribute(alert);
            return "signup";
        }
        userService.newUser(user, Role.ROLE_USER);
        return "redirect:/";
    }

    @RequestMapping(value = "/show/{username}", method = RequestMethod.GET)
    public String showUserPage(@PathVariable("username") String username, Model model, Principal principal) {
        User profileuser = userService.findUser(username);
        User user;
        if (principal == null) {
            user = new User();
        }
        else user = userService.findUser(principal.getName());
        List<Post> postList = postService.findByUsername(username);
        List<User> followersList = userService.findFollowers(username);
        if (followersList == null) followersList = new ArrayList<>();
        model.addAttribute(postList);
        model.addAttribute("profileUser", profileuser);
        model.addAttribute(user);
        model.addAttribute("followersList", followersList);
        return "profile";
    }

    @RequestMapping(value = "/follow", method = RequestMethod.GET)
    @Transactional
    public String followUser(@RequestParam("user") String username, Principal principal) {
        userService.follow(principal.getName(), username);
        return "redirect:/";
    }

    @RequestMapping(value = "/unfollow", method = RequestMethod.GET)
    @Transactional
    public String unfollowUser(@RequestParam("user") String username, Principal principal) {
        userService.unfollow(principal.getName(), username);
        return "redirect:/";
    }

}
