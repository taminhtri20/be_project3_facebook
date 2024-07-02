package org.example.be.controller;
import org.example.be.dto.ResponseDTO;
import org.example.be.modal.JwtResponse;
import org.example.be.modal.JwtToken;
import org.example.be.modal.User;
import org.example.be.respository.TokenRespository;
import org.example.be.service.RoleService;
import org.example.be.service.UserService;
import org.example.be.service.impl.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@CrossOrigin("*")
public class Controller {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    TokenRespository tokenRespository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/users")
    public ResponseEntity<Iterable<User>> showAllUser() {
        Iterable<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/admin/users")
    public ResponseEntity<Iterable<User>> showAllUserByAdmin() {
        Iterable<User> users = userService.findAll();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity createUser(@RequestBody User user) {
        if (userService.checkRegister(user).equals("success")){
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.save(user);
            return new ResponseEntity<>("Registered successfully", HttpStatus.CREATED);
        }else {
            return new ResponseEntity<>(userService.checkRegister(user), HttpStatus.OK);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtService.generateTokenLogin(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User currentUser = userService.findByUsername(user.getEmail());
            return ResponseEntity.ok(new ResponseDTO("200", "Login Success",
                    new JwtResponse(jwt, currentUser.getId(), currentUser.getFirstName()  + currentUser.getLastName(), userDetails.getAuthorities())));
        } catch (Exception e) {
            return ResponseEntity.ok(new ResponseDTO("401", "Email or password not existed", null));
        }
    }

    @PostMapping("/logoutUser")
    public ResponseEntity<?> logout(@RequestParam(name = "token")String token) {
        try {
            JwtToken jwtToken = tokenRespository.findByTokenEquals(token);
            if (jwtToken != null) {
                jwtToken.setValid(false);
                tokenRespository.save(jwtToken);
                return new ResponseEntity<>("Logout success", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid token", HttpStatus.OK);
            }
        }catch (Exception e){
            return new ResponseEntity<>("Invalid token", HttpStatus.OK);
        }
    }


    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return new ResponseEntity("Hello World", HttpStatus.OK);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getProfile(@PathVariable Long id) {
        Optional<User> userOptional = this.userService.findById(id);
        return new ResponseEntity<>(userOptional.get(), HttpStatus.OK);
    }
}
