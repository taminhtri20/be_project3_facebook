package org.example.be.service;

import org.example.be.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    void save(User user);

    Iterable<User> findAll();

    User findByUsername(String username);

    User getCurrentUser();

    Optional<User> findById(Long id);

    UserDetails loadUserById(Long id);
    boolean checkLogin(User user);

    boolean isRegister(User user);

    String checkRegister(User user);

    boolean checkEmail(String email);
    List<User> findByUsernameContaining(String username);
}