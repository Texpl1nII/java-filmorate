package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.impl.MpaRatingDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import(MpaRatingDbStorage.class)
class MpaRatingDbStorageTest {
    private final MpaRatingDbStorage mpaRatingStorage;

    @Test
    void testFindAllMpaRatings() {
        List<MpaRating> ratings = mpaRatingStorage.findAll();
        assertThat(ratings).hasSize(5);
        assertThat(ratings).anyMatch(r -> r.getName().equals("G"));
    }

    @Test
    void testFindMpaRatingById() {
        Optional<MpaRating> ratingOptional = mpaRatingStorage.findById(1);
        assertThat(ratingOptional)
                .isPresent()
                .hasValueSatisfying(r -> assertThat(r).hasFieldOrPropertyWithValue("name", "G"));
    }
}