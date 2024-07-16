package org.example.be.controller;

import org.example.be.model.Friend;
import org.example.be.model.User;
import org.example.be.service.FriendService;
import org.example.be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/friend")
public class FriendController {
    @Autowired
    FriendService friendService;
    @Autowired
    UserService userService;

    @GetMapping("/checkFriend")
    public ResponseEntity<?> checkFriend(@RequestParam(name = "currentUser")Long currentUserId, @RequestParam(name = "friendUser")Long friendUserId) {
        if (friendService.getFriends().size() == 0){
            return new ResponseEntity<>("Add" ,HttpStatus.OK);
        }
        for (Friend friend : friendService.getFriends()) {
            if (friend.getUserReceive().getId() == currentUserId ||
                    friend.getUserRequest().getId() == currentUserId &&
                            friend.getUserReceive().getId() == friendUserId ||
                                friend.getUserRequest().getId() == friendUserId){
                return new ResponseEntity<>(friend, HttpStatus.OK);
            }else {
                return new ResponseEntity<>("Add" ,HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Not Accepted" ,HttpStatus.OK);
    }

    @PostMapping("/addFriend")
    public ResponseEntity<?> addFriend(@RequestParam(name = "currentUser")Long currentUserId, @RequestParam(name = "friendUser")Long friendUserId) {
        User userRequest = userService.findById(currentUserId).get();
        User userReceive = userService.findById(friendUserId).get();
        friendService.addFriend(new Friend(userReceive, userRequest, false));
        return new ResponseEntity<>("Add Success", HttpStatus.CREATED);
    }

    @PostMapping("/acceptFriend")
    public ResponseEntity<?> acceptFriend(@RequestParam(name = "currentUser")Long currentUserId, @RequestParam(name = "friendUser")Long friendUserId) {
        User userRequest = userService.findById(currentUserId).get();
        User userReceive = userService.findById(friendUserId).get();
        for (Friend friend : friendService.getFriends()) {
            if (friend.getUserReceive().getId() == currentUserId ||
                    friend.getUserRequest().getId() == currentUserId &&
                            friend.getUserReceive().getId() == friendUserId ||
                    friend.getUserRequest().getId() == friendUserId){
                friend.setUserReceive(userReceive);
                friend.setUserRequest(userRequest);
                friend.setStatus(true);
                friendService.addFriend(friend);
                return new ResponseEntity<>("Accept Success", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Accept failed", HttpStatus.OK);
    }

    @PostMapping("/unFriend")
    public ResponseEntity<?> unFriend(@RequestParam(name = "currentUser")Long currentUserId, @RequestParam(name = "friendUser")Long friendUserId){
        for (Friend friend : friendService.getFriends()) {
            if (friend.getUserReceive().getId() == currentUserId ||
                    friend.getUserRequest().getId() == currentUserId &&
                            friend.getUserReceive().getId() == friendUserId ||
                                friend.getUserRequest().getId() == friendUserId){
                friendService.removeFriend(friend.getId());
                return new ResponseEntity<>("Unfriend Success", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Unfriend Success", HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> search(@RequestParam(name = "searchValue")String searchValue){
        return new ResponseEntity<>(userService.findByUsernameContaining(searchValue), HttpStatus.OK);
    }
}
