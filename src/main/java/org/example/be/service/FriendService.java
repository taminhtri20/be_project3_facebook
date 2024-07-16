package org.example.be.service;

import org.example.be.model.Friend;
import org.example.be.model.User;

import java.util.List;

public interface FriendService {
    void addFriend(Friend friend);
    void removeFriend(Long id);
    List<Friend> getFriends();
    List<User> getFriendsByUserId(Long userId);
}
