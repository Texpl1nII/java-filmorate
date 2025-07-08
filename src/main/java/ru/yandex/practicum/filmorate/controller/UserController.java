package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final ConcurrentHashMap<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    @GetMapping
    public List<User> findAll() {
        log.info("Returning all users, count: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.debug("Creating new user: {}", user.getEmail());
        user.setId(idGenerator.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Created user with id: {}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Updating user with id: {}", user.getId());
        if (user.getId() == null || user.getId() <= 0) {
            log.error("Invalid user ID: {}", user.getId());
            throw new IllegalArgumentException("User ID must be specified and positive");
        }
        if (!users.containsKey(user.getId())) {
            log.error("User with id {} not found", user.getId());
            throw new IllegalArgumentException("User with id " + user.getId() + " not found");
        }
        users.put(user.getId(), user);
        log.info("Updated user with id: {}", user.getId());
        return user;
    }
}
