package org.example.be.service.impl;

import org.example.be.model.Post;
import org.example.be.model.User;
import org.example.be.respository.PostRespository;
import org.example.be.service.FriendService;
import org.example.be.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostServiceIMPL implements PostService {
    @Autowired
    private PostRespository postRespository;
    @Autowired
    FriendService friendService;

    @Override
    public void save(Post post) {
        post.setCreateAt(LocalDateTime.now());
        postRespository.save(post);
    }

    @Override
    public List<Post> findAll() {
        return postRespository.findAll().stream()
                .sorted((post1, post2) -> post2.getCreateAt().compareTo(post1.getCreateAt()))
                .collect(Collectors.toList());
    }

    @Override
    public Post findById(Long id) {
        return postRespository.findById(id).get();
    }

    @Override
    public void delete(Long id) {
        postRespository.deleteById(id);
    }

    @Override
    public void update(Post post) {
        post.setCreateAt(LocalDateTime.now());
        postRespository.save(post);
    }

    @Override
    public List<Post> findByUserId(Long userId) {
        List<Post> posts = new ArrayList<>();
        for (Post post : postRespository.findAll()) {
            if (post.getUser().getId().equals(userId)){
                posts.add(post);
            }
            for (User user : friendService.getFriendsByUserId(userId)){
                if (post.getUser().getId().equals(user.getId())){
                    posts.add(post);
                }
            }
        }
        posts.stream().sorted((post1, post2) -> post2.getCreateAt().compareTo(post1.getCreateAt()))
                .collect(Collectors.toList());
        return posts;
    }

    @Override
    public List<Post> search(String keyword) {
        return postRespository.findAllByTitleContaining(keyword);
    }
}
