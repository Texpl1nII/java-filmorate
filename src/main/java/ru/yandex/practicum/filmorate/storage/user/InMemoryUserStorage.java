package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final ConcurrentHashMap<Long, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Set<Long>> friendships = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(0);

    @Override
    public User add(User user) {
        user.setId(idGenerator.incrementAndGet());
        users.put(user.getId(), user);
        friendships.putIfAbsent(user.getId(), ConcurrentHashMap.newKeySet());
        return user;
    }

    @Override
    public User update(User user) {
        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User with id " + user.getId() + " not found");
        }
        users.put(user.getId(), user);
        friendships.putIfAbsent(user.getId(), ConcurrentHashMap.newKeySet());
        return user;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get((long) id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));
        friendships.computeIfAbsent((long) userId, k -> ConcurrentHashMap.newKeySet()).add((long) friendId);
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + friendId + " not found"));
        Set<Long> friends = friendships.get((long) userId);
        if (friends != null) {
            friends.remove((long) friendId);
        }
    }

    @Override
    public List<User> getFriends(int userId) {
        return List.of();
    }

    @Override
    public List<User> getFriends(long userId) {
        findById((long) userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        Set<Long> friendIds = friendships.getOrDefault((long) userId, Set.of());
        return friendIds.stream()
                .map((Long id) -> findById((long) Math.toIntExact(id)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        findById(otherId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + otherId + " not found"));
        Set<Long> userFriends = friendships.getOrDefault((long) userId, Set.of());
        Set<Long> otherFriends = friendships.getOrDefault((long) otherId, Set.of());
        return userFriends.stream()
                .filter(otherFriends::contains)
                .map((Long id) -> findById((long) Math.toIntExact(id)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }
}