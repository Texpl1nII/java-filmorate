package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserStorage userStorage;

    public UserController(UserService userService, UserStorage userStorage) {
        this.userService = userService;
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Returning all users, count: {}", userStorage.findAll().size());
        return userStorage.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable int id) {
        log.debug("Finding user with id: {}", id);
        return userStorage.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Creating new user: {}", user.getEmail());
        return userStorage.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Updating user with id: {}", user.getId());
        if (user.getId() == null || user.getId() <= 0) {
            log.error("Invalid user ID: {}", user.getId());
            throw new ValidationException("User ID must be specified and positive");
        }
        return userStorage.update(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.debug("User {} adding friend {}", id, friendId);
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable int id, @PathVariable int friendId) {
        log.debug("User {} removing friend {}", id, friendId);
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.debug("Requesting friends for user {}", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.debug("Requesting common friends for users {} and {}", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}

