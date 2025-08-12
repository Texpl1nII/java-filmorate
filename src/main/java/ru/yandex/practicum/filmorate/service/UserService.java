package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public Optional<User> findById(int id) {
        return userStorage.findById(id);
    }

    public User add(User user) {
        if (user.getFriends() == null) {
            user.setFriends(new HashSet<>());
        }
        return userStorage.add(user);
    }

    public User update(User user) {
        User existingUser = findById(Math.toIntExact(user.getId()))
                .orElseThrow(() -> new EntityNotFoundException("User with id " + user.getId() + " not found"));

        if (user.getFriends() == null) {
            user.setFriends(existingUser.getFriends());
        }

        return userStorage.update(user);
    }

    public void addFriend(int userId, int friendId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        User friend = findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + friendId + " not found"));

        userStorage.addFriend(userId, friendId);

        user.addFriend((long) friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        User friend = findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + friendId + " not found"));

        userStorage.removeFriend(userId, friendId);

        user.removeFriend((long) friendId);
    }

    public List<User> getFriends(int userId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        return userStorage.getFriends(userId);
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        User otherUser = findById(otherId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + otherId + " not found"));

        return userStorage.getCommonFriends(userId, otherId);
    }
}