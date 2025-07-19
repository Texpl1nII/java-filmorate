package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(int userId, int friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));
        user.getFriends().add((long) friendId);
        friend.getFriends().add((long) userId);
        log.info("User {} added friend {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        User friend = userStorage.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));
        user.getFriends().remove((long) friendId);
        friend.getFriends().remove((long) userId);
        log.info("User {} removed friend {}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        log.info("Returning friends for user {}: {}", userId, user.getFriends().size());
        return user.getFriends().stream()
                .map(id -> userStorage.findById(id.intValue())
                        .orElse(null))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        User other = userStorage.findById(otherId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + otherId + " not found"));
        Set<Long> commonFriends = user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.toSet());
        log.info("Returning common friends for users {} and {}: {}", userId, otherId, commonFriends.size());
        return commonFriends.stream()
                .map(id -> userStorage.findById(id.intValue())
                        .orElse(null))
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }
}
