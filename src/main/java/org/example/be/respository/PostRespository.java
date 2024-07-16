package org.example.be.respository;

import org.example.be.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRespository extends JpaRepository<Post, Long> {
    List<Post> findAllByTitleContaining(String title);
}
