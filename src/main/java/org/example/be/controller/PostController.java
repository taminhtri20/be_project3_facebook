package org.example.be.controller;

import org.example.be.modal.Post;
import org.example.be.modal.User;
import org.example.be.respository.PostRespository;
import org.example.be.service.PostService;
import org.example.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/post")
public class PostController {
    @Autowired
    PostService postService;
    @Autowired
    PostRespository postRespository;
    @Autowired
    UserService userService;

    @GetMapping("/findAllPost")
    public ResponseEntity<List<Post>> findAllPost() {
        return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/findPostById")
    public Post findPostById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PostMapping("/savePost")
    public ResponseEntity<?> savePost(@RequestBody Post post) {
        User user = userService.findById(post.getUser().getId()).get();
        post.setUser(user);
        postService.save(post);
        return new ResponseEntity<>("Upload success", HttpStatus.OK);
    }

    @PutMapping("/editPost")
    public ResponseEntity<?> editPost(@RequestBody Post post) {
        postService.update(post);
        return new ResponseEntity<>("Update success", HttpStatus.OK);
    }

    @PutMapping("/likePost")
    public ResponseEntity<List<Post>> likePost(@RequestBody Post post, @RequestParam(name = "idUserLike") Long userId) {
        User user = userService.findById(userId).get();
        if (post.getUsersLiked().size() == 0){
            List<User> userList = new ArrayList<>();
            userList.add(user);
            post.setUsersLiked((userList));
        }else {
            post.getUsersLiked().add(user);
        }
        postRespository.save(post);
        return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
    }

    @PutMapping("/unlikePost")
    public ResponseEntity<?> unlikePost(@RequestBody Post post, @RequestParam(name = "idUserLike") Long userId) {
        User user = userService.findById(userId).get();
        if (post.getUsersLiked().size() == 0){
            return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
        }else {
            for (User u : post.getUsersLiked()) {
                if (u.getId().equals(user.getId())){
                    post.getUsersLiked().remove(u);
                    postRespository.save(post);
                    return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
    }
}
