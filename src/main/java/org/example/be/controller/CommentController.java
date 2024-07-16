package org.example.be.controller;

import org.example.be.model.Comment;
import org.example.be.model.Post;
import org.example.be.model.User;
import org.example.be.respository.CommentRepository;
import org.example.be.respository.PostRespository;
import org.example.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/comment")
public class CommentController {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private PostRespository postRespository;
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Comment comment, @RequestParam(name = "postId")Long postId) {
        User user = userService.findById(comment.getUser().getId()).get();
        Post post = postRespository.findById(postId).get();
        comment.setUser(user);
        commentRepository.save(comment);
        if (post.getComments().size() == 0){
            List<Comment> comments = new ArrayList<>();
            comments.add(comment);
            post.setComments(comments);
        }else {
            post.getComments().add(comment);
        }
        postRespository.save(post);
        return new ResponseEntity<>("Comment Success", HttpStatus.OK);
    }
}
