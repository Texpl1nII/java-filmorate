package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> findAll() {
        log.info("Returning all users, count: {}", userService.findAll().size());
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable int id) {
        log.debug("Finding user with id: {}", id);
        return userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));
    }

    @GetMapping("/{id}/friends/{friendId}")
    public User getFriend(@PathVariable int id, @PathVariable int friendId) {
        log.debug("Getting friend {} of user {}", friendId, id);
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + id + " not found"));

        User friend = userService.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));

        if (!user.getFriends().contains(friend.getId())) {
            throw new IllegalArgumentException("User " + friendId + " is not a friend of user " + id);
        }

        return friend;
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Creating new user: {}", user.getEmail());
        user.ensureValidName();
        return userService.add(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Updating user with id: {}", user.getId());
        user.ensureValidName();
        userService.findById(Math.toIntExact(user.getId()))
                .orElseThrow(() -> new IllegalArgumentException("User with id " + user.getId() + " not found"));
        return userService.update(user);
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