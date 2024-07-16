package org.example.be.service.impl;

import org.example.be.model.Friend;
import org.example.be.model.User;
import org.example.be.respository.FriendRepository;
import org.example.be.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FriendServiceIMPL implements FriendService {
    @Autowired
    FriendRepository friendRepository;
    @Override
    public void addFriend(Friend friend) {
        friendRepository.save(friend);
    }

    @Override
    public void removeFriend(Long id) {
        friendRepository.deleteById(id);
    }

    @Override
    public List<Friend> getFriends() {
        return friendRepository.findAll();
    }

    @Override
    public List<User> getFriendsByUserId(Long userId) {
        List<User> users = new ArrayList<>();
        for (Friend friend : friendRepository.findAll()) {
            if (friend.getUserRequest().getId().equals(userId)) {
                users.add(friend.getUserReceive());
            }
            if (friend.getUserReceive().getId().equals(userId)) {
                users.add(friend.getUserRequest());
            }
        }
        return users;
    }
}
