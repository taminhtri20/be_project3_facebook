package org.example.be.controller;

import org.example.be.model.Post;
import org.example.be.model.User;
import org.example.be.respository.PostRespository;
import org.example.be.service.PostService;
import org.example.be.service.UserService;
import org.example.be.service.impl.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @Autowired
    FileStorageService fileStorageService;

    @GetMapping("/findAllPost")
    public ResponseEntity<List<Post>> findAllPost() {
        return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/findPostByUser")
    public ResponseEntity<List<Post>> findPostByUser(@RequestParam(name = "id") Long id) {
        return new ResponseEntity<>(postService.findByUserId(id), HttpStatus.OK);
    }

    @GetMapping("/findPostById")
    public Post findPostById(@PathVariable Long id) {
        return postService.findById(id);
    }

    @PostMapping("/savePost")
    public ResponseEntity<?> savePost(@RequestParam(value = "file", required = false) MultipartFile file,
                                      @RequestParam("title") String title,
                                      @RequestParam("content") String content,
                                      @RequestParam("userId") Long userId) {
        try {
            Post post = new Post();
            post.setTitle(title);
            if (file != null){
                String fileName = fileStorageService.storeFile(file);
                post.setContent(fileName); // Lưu tên file vào nội dung bài đăng (đây là chỉ dẫn, bạn có thể lưu tùy ý)
            }else {
                post.setContent("");
            }

            User user = userService.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            post.setUser(user);

            postService.save(post);

            return ResponseEntity.ok("Upload success");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed");
        }
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
            for(User u : post.getUsersLiked()) {
                if (u.getId().equals(user.getId())){
                    post.getUsersLiked().remove(u);
                    postRespository.save(post);
                    return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
                }
            }
        }
        return new ResponseEntity<>(postService.findAll(), HttpStatus.OK);
    }

    @DeleteMapping("/deletePost")
    public ResponseEntity<?> deletePost(@RequestParam(name = "id") Long id) {
        postService.delete(id);
        return new ResponseEntity<>("Delete success", HttpStatus.OK);
    }

        @GetMapping("/searchingPost")
    public ResponseEntity<List<Post>> searchingPost(@RequestParam(name = "searchValue") String searchValue) {
        return new ResponseEntity<>(postService.search(searchValue), HttpStatus.OK);
    }
}
