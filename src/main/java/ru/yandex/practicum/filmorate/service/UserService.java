package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserStorage userStorage;
    private Long friendId;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> findAll() {
        return userStorage.findAll();
    }

    public Optional<User> findById(Long id) {
        return userStorage.findById((long) id);
    }

    public User add(User user) {
        validateUser(user);
        return userStorage.add(user);
    }

    public User update(User user) {
        validateUser(user);
        userStorage.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with id " + user.getId() + " not found"));
        return userStorage.update(user);
    }

    private void validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    public void addFriend(Long userId, Long friendId) {
        this.friendId = friendId;
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        User friend = findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + friendId + " not found"));

        userStorage.addFriend(userId, friendId);

        user.addFriend((long) friendId);
    }

    public void removeFriend(Long userId, Long friendId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        User friend = findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + friendId + " not found"));

        userStorage.removeFriend(userId, friendId);

        user.removeFriend((long) friendId);
    }

    public List<User> getCommonFriends(Long userId, Long otherId) {
        User user = findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));
        User otherUser = findById(otherId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + otherId + " not found"));

        return userStorage.getCommonFriends(userId, otherId);
    }

    public User getFriends(Long userId) {
        User user = userStorage.findById((long) Math.toIntExact(userId))
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        return userStorage.findById((long) Math.toIntExact(friendId))
                .orElseThrow(() -> new EntityNotFoundException("Friend with id " + friendId + " not found"));
    }

    public User getFriend(Long userId, Long friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + userId + " not found"));

        return userStorage.findById(friendId)
                .orElseThrow(() -> new EntityNotFoundException("Friend with id " + friendId + " not found"));
    }
}