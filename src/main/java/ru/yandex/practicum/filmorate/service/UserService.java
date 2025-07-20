package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User add(User user) {
        log.debug("Adding user: {}", user.getEmail());
        return userStorage.add(user);
    }

    public User update(User user) {
        log.debug("Updating user with id: {}", user.getId());
        return userStorage.update(user);
    }

    public Optional<User> findById(int id) {
        log.debug("Finding user with id: {}", id);
        return userStorage.findById(id);
    }

    public List<User> findAll() {
        log.debug("Returning all users, count: {}", userStorage.findAll().size());
        return userStorage.findAll();
    }

    public void addFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add((long) friendId);
        friend.getFriends().add((long) userId);
        log.info("User {} added friend {}", userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        User user = getUserOrThrow(userId);
        User friend = getUserOrThrow(friendId);
        user.getFriends().remove((long) friendId);
        friend.getFriends().remove((long) userId);
        log.info("User {} removed friend {}", userId, friendId);
    }

    public List<User> getFriends(int userId) {
        User user = getUserOrThrow(userId);
        log.info("Returning friends for user {}: {}", userId, user.getFriends().size());
        return user.getFriends().stream()
                .map(id -> userStorage.findById(id.intValue())
                        .orElse(null))
                .filter(user1 -> user != null)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        User user = getUserOrThrow(userId);
        User other = getUserOrThrow(otherId);
        Set<Long> commonFriends = user.getFriends().stream()
                .filter(other.getFriends()::contains)
                .collect(Collectors.toSet());
        log.info("Returning common friends for users {} and {}: {}", userId, otherId, commonFriends.size());
        return commonFriends.stream()
                .map(id -> userStorage.findById(id.intValue())
                        .orElse(null))
                .filter(user1 -> user != null)
                .collect(Collectors.toList());
    }

    public User getUserOrThrow(int id) {
        Optional<User> user = userStorage.findById(id);
        if (user.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new IllegalArgumentException("User with id " + id + " not found");
        }
        return user.get();
    }
}
