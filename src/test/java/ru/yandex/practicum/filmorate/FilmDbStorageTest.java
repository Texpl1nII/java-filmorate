package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaRatingId(1);
        film.setGenreIds(Set.of(1, 2));

        Film savedFilm = filmStorage.add(film);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isGreaterThan(0);
        assertThat(savedFilm.getGenreIds()).contains(1, 2);
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaRatingId(1);
        Film savedFilm = filmStorage.add(film);

        savedFilm.setName("Updated Film");
        savedFilm.setGenreIds(Set.of(3));
        Film updatedFilm = filmStorage.update(savedFilm);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Film");
        assertThat(updatedFilm.getGenreIds()).containsExactly(3);
    }

    @Test
    void testFindFilmById() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaRatingId(1);
        Film savedFilm = filmStorage.add(film);

        Optional<Film> filmOptional = filmStorage.findById(savedFilm.getId());
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(f -> assertThat(f).hasFieldOrPropertyWithValue("id", savedFilm.getId()));
    }

    @Test
    void testFindAllFilms() {
        Film film1 = new Film();
        film1.setName("Test Film1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpaRatingId(1);
        filmStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Test Film2");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(100);
        film2.setMpaRatingId(2);
        filmStorage.add(film2);

        List<Film> films = filmStorage.findAll();
        assertThat(films).hasSize(2);
    }

    @Test
    void testAddLike() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.add(user);

        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaRatingId(1);
        Film savedFilm = filmStorage.add(film);

        filmStorage.addLike(savedFilm.getId(), savedUser.getId());
        Optional<Film> filmOptional = filmStorage.findById(savedFilm.getId());
        assertThat(filmOptional.get().getLikes()).contains(savedUser.getId());
    }

    @Test
    void testRemoveLike() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.add(user);

        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        film.setMpaRatingId(1);
        Film savedFilm = filmStorage.add(film);

        filmStorage.addLike(savedFilm.getId(), savedUser.getId());
        filmStorage.removeLike(savedFilm.getId(), savedUser.getId());
        Optional<Film> filmOptional = filmStorage.findById(savedFilm.getId());
        assertThat(filmOptional.get().getLikes()).doesNotContain(savedUser.getId());
    }

    @Test
    void testGetPopularFilms() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.add(user);

        Film film1 = new Film();
        film1.setName("Test Film1");
        film1.setDescription("Description1");
        film1.setReleaseDate(LocalDate.of(2000, 1, 1));
        film1.setDuration(120);
        film1.setMpaRatingId(1);
        Film savedFilm1 = filmStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Test Film2");
        film2.setDescription("Description2");
        film2.setReleaseDate(LocalDate.of(2001, 1, 1));
        film2.setDuration(100);
        film2.setMpaRatingId(2);
        Film savedFilm2 = filmStorage.add(film2);

        filmStorage.addLike(savedFilm1.getId(), savedUser.getId());
        List<Film> popularFilms = filmStorage.getPopularFilms(1);
        assertThat(popularFilms).hasSize(1);
        assertThat(popularFilms.get(0).getId()).isEqualTo(savedFilm1.getId());
    }
}