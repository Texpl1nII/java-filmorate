package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private Set<Long> getFriendIds(long userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friend_id"), userId));
    }
    @Override
    public User add(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);
        user.setId(keyHolder.getKey().longValue());
        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(),
                user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public Optional<User> findById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
            if (user != null) {
                user.setFriends(getFriendIds(id));
            }
            return Optional.ofNullable(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        for (User user : users) {
            user.setFriends(getFriendIds(Math.toIntExact(user.getId())));
        }
        return users;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        // Проверка существования пользователей
        findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User with id " + userId + " not found"));
        findById(friendId).orElseThrow(() ->
                new IllegalArgumentException("User with id " + friendId + " not found"));

        // Добавление записи о дружбе
        String sql = "INSERT INTO friends (user_id, friend_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User with id " + userId + " not found"));
        findById(friendId).orElseThrow(() ->
                new IllegalArgumentException("User with id " + friendId + " not found"));

        String sql = "DELETE FROM friends WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public List<User> getFriends(int userId) {
        findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User with id " + userId + " not found"));

        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f ON u.user_id = f.friend_id " +
                "WHERE f.user_id = ?";
        List<User> friends = jdbcTemplate.query(sql, this::mapRowToUser, userId);

        for (User friend : friends) {
            friend.setFriends(getFriendIds(Math.toIntExact(friend.getId())));
        }

        return friends;
    }

    @Override
    public List<User> getCommonFriends(int userId, int otherUserId) {
        findById(userId).orElseThrow(() ->
                new IllegalArgumentException("User with id " + userId + " not found"));
        findById(otherUserId).orElseThrow(() ->
                new IllegalArgumentException("User with id " + otherUserId + " not found"));

        String sql = "SELECT u.* FROM users u " +
                "JOIN friends f1 ON u.user_id = f1.friend_id " +
                "JOIN friends f2 ON u.user_id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        List<User> commonFriends = jdbcTemplate.query(sql, this::mapRowToUser, userId, otherUserId);

        for (User friend : commonFriends) {
            friend.setFriends(getFriendIds(Math.toIntExact(friend.getId())));
        }

        return commonFriends;
    }

    private User mapRowToUser(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        user.setFriends(new HashSet<>());
        return user;
    }

    private Set<Long> getFriendIds(int userId) {
        String sql = "SELECT friend_id FROM friends WHERE user_id = ?";
        List<Long> friendIds = jdbcTemplate.queryForList(sql, Long.class, userId);
        return new HashSet<>(friendIds);
    }
}