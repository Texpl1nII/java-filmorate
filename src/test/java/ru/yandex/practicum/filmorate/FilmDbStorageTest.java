package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class})
@Sql(scripts = {"classpath:schema.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    @Test
    void testAddFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("This is a test film.");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "MPA Rating"));
        film.setGenres(List.of(new Genre(1L, "Action")));

        Film savedFilm = filmStorage.add(film);
        assertThat(savedFilm).isNotNull();
        assertThat(savedFilm.getId()).isGreaterThan(0);
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Original Title");
        film.setDescription("Original Description");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "MPA Rating"));
        film.setGenres(List.of(new Genre(1L, "Action")));

        Film savedFilm = filmStorage.add(film);

        savedFilm.setName("Updated Title");
        savedFilm.setGenres(List.of(new Genre(2L, "Comedy")));

        Film updatedFilm = filmStorage.update(savedFilm);
        assertThat(updatedFilm.getName()).isEqualTo("Updated Title");
        assertThat(updatedFilm.getGenres()).extracting(Genre::getId).containsExactlyInAnyOrder(2L);
    }

    @Test
    void testFindFilmById() {
        Film film = new Film();
        film.setName("Searchable Film");
        film.setDescription("Testing search by ID.");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "MPA Rating"));
        film.setGenres(List.of(new Genre(1L, "Action")));

        Film savedFilm = filmStorage.add(film);
        Optional<Film> foundFilm = filmStorage.findById(savedFilm.getId());
        assertThat(foundFilm).isPresent();
        assertThat(foundFilm.get().getName()).isEqualTo("Searchable Film");
    }

    @Test
    void testFindAllFilms() {
        Film film1 = new Film();
        film1.setName("First Film");
        film1.setDescription("First movie.");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new MpaRating(1L, "MPA Rating"));
        film1.setGenres(List.of(new Genre(1L, "Action")));
        filmStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Second Film");
        film2.setDescription("Second movie.");
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));
        film2.setDuration(150);
        film2.setMpa(new MpaRating(2L, "Another MPA Rating"));
        film2.setGenres(List.of(new Genre(2L, "Drama")));
        filmStorage.add(film2);

        List<Film> allFilms = filmStorage.findAll();
        assertThat(allFilms).hasSize(2);
    }

    @Test
    void testAddAndRemoveLike() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("test_login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.add(user);

        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Test movie.");
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setDuration(120);
        film.setMpa(new MpaRating(1L, "MPA Rating"));
        film.setGenres(List.of(new Genre(1L, "Action")));
        Film savedFilm = filmStorage.add(film);

        filmStorage.addLike(savedFilm.getId(), savedUser.getId());
        Optional<Film> likedFilm = filmStorage.findById(savedFilm.getId());
        assertThat(likedFilm.get().getLikes()).contains(savedUser.getId());

        filmStorage.removeLike(savedFilm.getId(), savedUser.getId());
        Optional<Film> unlikedFilm = filmStorage.findById(savedFilm.getId());
        assertThat(unlikedFilm.get().getLikes()).doesNotContain(savedUser.getId());
    }

    @Test
    void testGetPopularFilms() {
        User user = new User();
        user.setEmail("test@test.ru");
        user.setLogin("test_login");
        user.setName("Test Name");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        User savedUser = userStorage.add(user);

        Film film1 = new Film();
        film1.setName("Most Liked Film");
        film1.setDescription("Movie with many likes.");
        film1.setReleaseDate(LocalDate.of(2020, 1, 1));
        film1.setDuration(120);
        film1.setMpa(new MpaRating(1L, "MPA Rating"));
        film1.setGenres(List.of(new Genre(1L, "Action")));
        Film savedFilm1 = filmStorage.add(film1);

        Film film2 = new Film();
        film2.setName("Less Liked Film");
        film2.setDescription("Movie with fewer likes.");
        film2.setReleaseDate(LocalDate.of(2021, 1, 1));
        film2.setDuration(150);
        film2.setMpa(new MpaRating(2L, "Another MPA Rating"));
        film2.setGenres(List.of(new Genre(2L, "Drama")));
        Film savedFilm2 = filmStorage.add(film2);

        filmStorage.addLike(savedFilm1.getId(), savedUser.getId());
        List<Film> popularFilms = filmStorage.getPopularFilms(1);
        assertThat(popularFilms).hasSize(1);
        assertThat(popularFilms.get(0).getId()).isEqualTo(savedFilm1.getId());
    }
}