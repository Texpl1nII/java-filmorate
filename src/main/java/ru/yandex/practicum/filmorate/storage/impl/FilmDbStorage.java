package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.PreparedStatement;
import java.util.*;

@Repository("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Film> filmRowMapper = (rs, rowNum) -> {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(new MpaRating(rs.getLong("mpa_rating_id"), rs.getString("mpa_name")));
        return film;
    };

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setObject(3, film.getReleaseDate());
            ps.setInt(4, film.getDuration());
            ps.setInt(5, Math.toIntExact(film.getMpa().getId()));
            return ps;
        }, keyHolder);
        film.setId(keyHolder.getKey().longValue());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Long> uniqueGenreIds = new HashSet<>();

            for (Genre genre : film.getGenres()) {
                if (uniqueGenreIds.add(genre.getId())) {
                    jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                            film.getId(), genre.getId());
                }
            }

            List<Genre> savedGenres = getGenresForFilm(film.getId());
            film.setGenres(savedGenres);
        }

        film.setLikes(new HashSet<>());
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            Set<Long> uniqueGenreIds = new HashSet<>();

            for (Genre genre : film.getGenres()) {
                if (uniqueGenreIds.add(genre.getId())) {
                    jdbcTemplate.update("INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)",
                            film.getId(), genre.getId());
                }
            }

            List<Genre> savedGenres = getGenresForFilm(film.getId());
            film.setGenres(savedGenres);
        } else {
            film.setGenres(new ArrayList<>());
        }

        return film;
    }

    @Override
    public Optional<Film> findById(Long id) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id " +
                "WHERE f.film_id = ?";
        try {
            Film film = jdbcTemplate.queryForObject(sql, filmRowMapper, id);
            if (film != null) {
                List<Genre> genres = getGenresForFilm(film.getId());
                film.setGenres(genres);
                film.setLikes(getLikesForFilm(film.getId()));
                return Optional.of(film);
            }
            return Optional.empty();
        } catch (EmptyResultDataAccessException exception) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper);
        for (Film film : films) {
            List<Genre> genres = getGenresForFilm(film.getId());
            film.setGenres(genres);
            film.setLikes(getLikesForFilm(film.getId()));
        }
        return films;
    }

    @Override
    public List<Film> getFilmsByGenre(Long genreId) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id " +
                "JOIN film_genres fg ON f.film_id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "ORDER BY f.film_id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, genreId);

        for (Film film : films) {
            List<Genre> genres = getGenresForFilm(film.getId());
            film.setGenres(genres);
            film.setLikes(getLikesForFilm(film.getId()));
        }

        return films;
    }

    private List<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.genre_id as id, g.name FROM film_genres fg " +
                "JOIN genres g ON fg.genre_id = g.genre_id WHERE fg.film_id = ? " +
                "ORDER BY g.genre_id";
        return jdbcTemplate.query(sql, (ResultSet, rowNum) ->
                new Genre(ResultSet.getLong("id"), ResultSet.getString("name")), filmId);
    }

    private Set<Long> getLikesForFilm(Long filmId) {
        String sql = "SELECT user_id FROM likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.queryForList(sql, Long.class, filmId));
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void addLike(int filmId, int userId) {

    }

    @Override
    public void removeLike(int filmId, int userId) {

    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, m.name AS mpa_name, COUNT(l.user_id) AS like_count " +
                "FROM films f JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id ORDER BY like_count DESC LIMIT ?";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, count);
        for (Film film : films) {
            film.setGenres(getGenresForFilm(film.getId()));
            film.setLikes(getLikesForFilm(film.getId()));
        }
        return films;
    }

    public List<Film> getFilmsByGenreWithoutDuplicates(long genreId) {
        String sql = "SELECT f.*, m.name AS mpa_name FROM films f " +
                "JOIN mpa_ratings m ON f.mpa_rating_id = m.mpa_rating_id " +
                "JOIN film_genres fg ON f.film_id = fg.film_id " +
                "WHERE fg.genre_id = ? " +
                "ORDER BY f.film_id";
        List<Film> films = jdbcTemplate.query(sql, filmRowMapper, genreId);

        for (Film film : films) {
            List<Genre> genres = getGenresForFilm(film.getId());

            boolean containsRequestedGenre = genres.stream()
                    .anyMatch(genre -> genre.getId() == genreId);

            if (!containsRequestedGenre) {
                Genre requestedGenre = jdbcTemplate.queryForObject(
                        "SELECT genre_id as id, name FROM genres WHERE genre_id = ?",
                        (ResultSet, rowNum) -> new Genre(ResultSet.getLong("id"), ResultSet.getString("name")),
                        genreId
                );
                genres.add(requestedGenre);
            }

            film.setGenres(genres);
            film.setLikes(getLikesForFilm(film.getId()));
        }

        return films;
    }
}