package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(GenreDbStorage.class)
class GenreDbStorageTest {
    private final GenreDbStorage genreStorage;

    @Test
    void testFindAllGenres() {
        List<Genre> genres = genreStorage.findAll();
        assertThat(genres).hasSize(6);
        assertThat(genres).anyMatch(g -> g.getName().equals("Комедия"));
    }

    @Test
    void testFindGenreById() {
        Optional<Genre> genreOptional = genreStorage.findById(1);
        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(g -> assertThat(g).hasFieldOrPropertyWithValue("name", "Комедия"));
    }
}