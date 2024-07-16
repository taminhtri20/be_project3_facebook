package org.example.be.service;

import org.example.be.model.Post;

import java.util.List;

public interface PostService {
    void save(Post post);
    List<Post> findAll();
    Post findById(Long id);
    void delete(Long id);
    void update(Post post);
    List<Post> findByUserId(Long userId);
    List<Post> search(String keyword);
}
