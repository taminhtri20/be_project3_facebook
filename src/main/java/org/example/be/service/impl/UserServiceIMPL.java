package org.example.be.service.impl;

import org.example.be.model.Role;
import org.example.be.model.User;
import org.example.be.model.UserPrinciple;
import org.example.be.respository.UserRespository;
import org.example.be.service.RoleService;
import org.example.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceIMPL implements UserService {
    @Autowired
    private UserRespository userRespository;
    @Autowired
    RoleService roleService;
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        User user = userRespository.findByEmailOrPhone(email, email);
        if (user == null) {
            throw new UsernameNotFoundException(email);
        }
        if (this.checkLogin(user)) {
            return UserPrinciple.build(user);
        }
        boolean enable = false;
        boolean accountNonExpired = false;
        boolean credentialsNonExpired = false;
        boolean accountNonLocked = false;
        return new org.springframework.security.core.userdetails.User(user.getEmail(),
                user.getPassword(), enable, accountNonExpired, credentialsNonExpired,
                accountNonLocked, null);
    }


    @Override
    public void save(User user) {
        userRespository.save(user);
    }

    @Override
    public Iterable<User> findAll() {
        return userRespository.findAll();
    }

    @Override
    public User findByUsername(String email) {
        return userRespository.findByEmailOrPhone(email, email);
    }

    @Override
    public User getCurrentUser() {
        User user;
        String userName;
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            userName = ((UserDetails) principal).getUsername();
        } else {
            userName = principal.toString();
        }
        user = this.findByUsername(userName);
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRespository.findById(id);
    }

    @Override
    public UserDetails loadUserById(Long id) {
        Optional<User> user = userRespository.findById(id);
        if (user.isEmpty()) {
            throw new NullPointerException();
        }
        return UserPrinciple.build(user.get());
    }

    @Override
    public boolean checkLogin(User user) {
        Iterable<User> users = this.findAll();
        boolean isCorrectUser = false;
        for (User currentUser : users) {
            if (currentUser.getEmail().equals(user.getEmail()) && user.getPassword().equals(currentUser.getPassword())) {
                isCorrectUser = true;
                break;
            }
        }
        return isCorrectUser;
    }

    @Override
    public boolean isRegister(User user) {
        boolean isRegister = false;
        Iterable<User> users = this.findAll();
        for (User currentUser : users) {
            if (user.getEmail().equals(currentUser.getEmail())) {
                isRegister = true;
                break;
            }
        }
        return isRegister;
    }

    @Override
    public String checkRegister(User user) {
        Iterable<User> users = this.findAll();
        if (users.iterator().hasNext() == false){
            Role role = roleService.findByName("ROLE_ADMIN");
            user.setRoles(Collections.singletonList(role));
        }else {
            Role role = roleService.findByName("ROLE_USER");
            user.setRoles(Collections.singletonList(role));
        }
        if (!checkEmail(user.getEmail()) && user.getEmail() != null) {
            user.setPhone(user.getEmail());
            user.setEmail(null);
        }
        for (User currentUser : users) {
            if (currentUser.getEmail().equals(user.getEmail())) {
                return "Email existed";
            }
            if (user.getPhone() != null && currentUser.getPhone().equals(user.getPhone())) {
                return "Phone existed";
            }
        }
        return "success";
    }

    @Override
    public boolean checkEmail(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(email);
        return matcher.matches();
    }

    @Override
    public List<User> findByUsernameContaining(String username) {
        String[] parts = username.split("\\s+");
        String firstName = "";
        String lastName = "";

        if (parts.length > 0) {
            firstName = parts[0];
        }
        if (parts.length > 1) {
            lastName = String.join(" ", Arrays.copyOfRange(parts, 1, parts.length)); // Gộp các phần tử từ index 1 đến hết thành lastName
        }else {
            lastName = parts[0];
        }
        return userRespository.findAllByFirstNameContainingOrLastNameContaining(firstName, lastName);
    }
}
