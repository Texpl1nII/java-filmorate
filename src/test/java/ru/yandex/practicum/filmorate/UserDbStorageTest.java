package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(UserDbStorage.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testAddUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        User savedUser = userStorage.add(user);
        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getId()).isGreaterThan(0);
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void testUpdateUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.add(user);

        savedUser.setName("Updated User");
        User updatedUser = userStorage.update(savedUser);
        assertThat(updatedUser.getName()).isEqualTo("Updated User");
    }

    @Test
    void testFindUserById() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.add(user);

        Optional<User> userOptional = userStorage.findById(Math.toIntExact(savedUser.getId()));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(u -> assertThat(u).hasFieldOrPropertyWithValue("id", savedUser.getId()));
    }

    @Test
    void testFindAllUsers() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("testLogin1");
        user1.setName("Test User1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        userStorage.add(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("testLogin2");
        user2.setName("Test User2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        userStorage.add(user2);

        List<User> users = userStorage.findAll();
        assertThat(users).hasSize(2);
    }

    @Test
    void testAddFriend() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("testLogin1");
        user1.setName("Test User1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser1 = userStorage.add(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("testLogin2");
        user2.setName("Test User2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User savedUser2 = userStorage.add(user2);

        userStorage.addFriend(Math.toIntExact(savedUser1.getId()), Math.toIntExact(savedUser2.getId()));
        List<User> friends = userStorage.getFriends(Math.toIntExact(savedUser1.getId()));
        assertThat(friends).hasSize(1);
        assertThat(friends.get(0).getId()).isEqualTo(savedUser2.getId());
    }

    @Test
    void testRemoveFriend() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("testLogin1");
        user1.setName("Test User1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser1 = userStorage.add(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("testLogin2");
        user2.setName("Test User2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User savedUser2 = userStorage.add(user2);

        userStorage.addFriend(Math.toIntExact(savedUser1.getId()), Math.toIntExact(savedUser2.getId()));
        userStorage.removeFriend(Math.toIntExact(savedUser1.getId()), Math.toIntExact(savedUser2.getId()));
        List<User> friends = userStorage.getFriends(Math.toIntExact(savedUser1.getId()));
        assertThat(friends).isEmpty();
    }

    @Test
    void testGetFriends() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("testLogin1");
        user1.setName("Test User1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser1 = userStorage.add(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("testLogin2");
        user2.setName("Test User2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User savedUser2 = userStorage.add(user2);

        userStorage.addFriend(Math.toIntExact(savedUser1.getId()), Math.toIntExact(savedUser2.getId()));
        List<User> friends = userStorage.getFriends(Math.toIntExact(savedUser1.getId()));
        assertThat(friends).hasSize(1);
    }

    @Test
    void testGetCommonFriends() {
        User user1 = new User();
        user1.setEmail("test1@example.com");
        user1.setLogin("testLogin1");
        user1.setName("Test User1");
        user1.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser1 = userStorage.add(user1);

        User user2 = new User();
        user2.setEmail("test2@example.com");
        user2.setLogin("testLogin2");
        user2.setName("Test User2");
        user2.setBirthday(LocalDate.of(1991, 1, 1));
        User savedUser2 = userStorage.add(user2);

        User user3 = new User();
        user3.setEmail("test3@example.com");
        user3.setLogin("testLogin3");
        user3.setName("Test User3");
        user3.setBirthday(LocalDate.of(1992, 1, 1));
        User savedUser3 = userStorage.add(user3);

        userStorage.addFriend(Math.toIntExact(savedUser1.getId()), Math.toIntExact(savedUser3.getId()));
        userStorage.addFriend(Math.toIntExact(savedUser2.getId()), Math.toIntExact(savedUser3.getId()));
        List<User> commonFriends = userStorage.getCommonFriends(Math.toIntExact(savedUser1.getId()), Math.toIntExact(savedUser2.getId()));
        assertThat(commonFriends).hasSize(1);
        assertThat(commonFriends.get(0).getId()).isEqualTo(savedUser3.getId());
    }
}