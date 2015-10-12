package me.molchanoff.microblog.controllers;

import me.molchanoff.microblog.model.Post;
import me.molchanoff.microblog.model.User;
import me.molchanoff.microblog.services.PostService;
import me.molchanoff.microblog.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import java.security.Principal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/post")
public class PostController {
    private PostService postService;
    private UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @RequestMapping(value = "/show/{postId}", method = RequestMethod.GET)
    public String showPost(@PathVariable("postId") long postId, Model model, Principal principal) {
        List<Post> postList = new ArrayList<>();
        User user = new User();
        if (principal != null) {
            user = userService.findUser(principal.getName());
        }
        postList.add(postService.findPost(postId));
        model.addAttribute(user);
        model.addAttribute(postList);
        return "post";
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public String showNewPage(Model model) {
        model.addAttribute(new Post());
        return "newpost";
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public String newPost(@Valid Post post, Errors errors, Principal principal) throws ParseException {
        if (errors.hasErrors()) {
            return "newpost";
        }
        User user = userService.findUser(principal.getName());
        post.setUser(user);
        postService.newPost(post);
        return "redirect:/";
    }

    @RequestMapping(value = "/vote", method = RequestMethod.GET)
    public String votePost(@RequestParam("postId") long id, Principal principal) {
        postService.votePost(id, principal.getName());
        return "redirect:/";
    }

    @RequestMapping(value = "/unvote", method = RequestMethod.GET)
    public String unvotePost(@RequestParam("postId") long id, Principal principal) {
        postService.unvotePost(id, principal.getName());
        return "redirect:/";
    }


}
