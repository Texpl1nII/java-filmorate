package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
public class User {
    private Long id;

    @Email(message = "Email must be valid")
    @NotEmpty(message = "Email must not be empty")
    private String email;

    @Pattern(regexp = "^\\S+$", message = "Login must not contain spaces")
    @NotBlank(message = "Login must not be empty")
    private String login;

    private String name;

    @PastOrPresent(message = "Birthday must not be in the future")
    private LocalDate birthday;

    private Set<Long> friends;

    public User(Long id, String email, String login, String name, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = (name == null || name.trim().isEmpty()) ? login : name;
        this.birthday = birthday;
        this.friends = friends != null ? friends : new HashSet<>();
    }

    public void addFriend(Long friendId) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(friendId);
    }

    public void removeFriend(Long friendId) {
        if (friends != null) {
            friends.remove(friendId);
        }
    }
}