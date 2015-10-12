package me.molchanoff.microblog.controllers;

import me.molchanoff.microblog.model.Alert;
import me.molchanoff.microblog.model.AlertType;
import me.molchanoff.microblog.model.Post;
import me.molchanoff.microblog.model.User;
import me.molchanoff.microblog.services.PostService;
import me.molchanoff.microblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/")
public class HomeController {
    private PostService postService;
    private UserService userService;

    @Autowired
    public HomeController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @RequestMapping(method = RequestMethod.GET)
	public String showHome() {
        return "redirect:/recent";
	}

    @RequestMapping(value = "/recent", method = RequestMethod.GET)
    public String showRecent(Model model, Principal principal) {
        List<Post> postList = postService.findAll();
        model.addAttribute(postList);
        User user = new User();
        if (principal != null) {
            user = userService.findUser(principal.getName());
        }
        model.addAttribute(user);
        return "recent";
    }

    @RequestMapping(value = "/myposts", method = RequestMethod.GET)
    public String showMyPosts(Model model, Principal principal) {
        User user = userService.findUser(principal.getName());
        List<Post> postList = postService.findByUsername(principal.getName());
        model.addAttribute(postList);
        model.addAttribute(user);
        return "myposts";
    }

    @RequestMapping(value = "/feed", method = RequestMethod.GET)
    public String showFeed(Model model, Principal principal) {
        User user = userService.findUser(principal.getName());
        List<Post> postList = postService.findFollowing(principal.getName());
        if (postList == null) postList = new ArrayList<>();
        model.addAttribute(postList);
        model.addAttribute(user);
        return "feed";
    }

    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String showProfile(Model model, Principal principal) {
        User user = userService.findUser(principal.getName());
        List<Post> postList = postService.findByUsername(principal.getName());
        List<User> followersList = userService.findFollowers(principal.getName());
        if (followersList == null) followersList = new ArrayList<>();
        model.addAttribute(postList);
        model.addAttribute(user);
        model.addAttribute("profileUser", user);
        model.addAttribute("followersList", followersList);
        return "profile";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String showLoginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            Alert alert = new Alert(AlertType.DANGER, "Error!", "Incorrect username or password");
            model.addAttribute(alert);
        }
        return "login";
    }

}