package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film add(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_rating_id) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setObject(5, film.getMpaRatingId());
            return ps;
        }, keyHolder);
        film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        saveGenres(film);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ? WHERE film_id = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpaRatingId(), film.getId());
        jdbcTemplate.update("DELETE FROM film_genres WHERE film_id = ?", film.getId());
        saveGenres(film);
        if (film.getLikes() == null) {
            film.setLikes(new HashSet<>());
        }
        return film;
    }

    @Override
    public Optional<Film> findById(int id) {
        String sql = "SELECT f.*, GROUP_CONCAT(fg.genre_id) AS genre_ids, GROUP_CONCAT(l.user_id) AS like_ids " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "WHERE f.film_id = ? GROUP BY f.film_id";
        List<Film> films = jdbcTemplate.query(sql, this::mapRowToFilm, id);
        return films.isEmpty() ? Optional.empty() : Optional.of(films.get(0));
    }

    @Override
    public List<Film> findAll() {
        String sql = "SELECT f.*, GROUP_CONCAT(fg.genre_id) AS genre_ids, GROUP_CONCAT(l.user_id) AS like_ids " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        String sql = "SELECT f.*, GROUP_CONCAT(fg.genre_id) AS genre_ids, GROUP_CONCAT(l.user_id) AS like_ids, COUNT(l.user_id) AS like_count " +
                "FROM films f " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN likes l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id ORDER BY like_count DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToFilm, count);
    }

    private void saveGenres(Film film) {
        if (film.getGenreIds() != null && !film.getGenreIds().isEmpty()) {
            String sql = "INSERT INTO film_genres (film_id, genre_id) VALUES (?, ?)";
            film.getGenreIds().forEach(genreId ->
                    jdbcTemplate.update(sql, film.getId(), genreId));
        }
    }

    private Film mapRowToFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpaRatingId(rs.getInt("mpa_rating_id"));
        if (rs.getInt("mpa_rating_id") == 0) {
            film.setMpaRatingId(null);
        }

        String genreIds = rs.getString("genre_ids");
        Set<Integer> genreIdSet = new HashSet<>();
        if (genreIds != null && !genreIds.isEmpty()) {
            Arrays.stream(genreIds.split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .forEach(genreIdSet::add);
        }
        film.setGenreIds(genreIdSet);

        String likeIds = rs.getString("like_ids");
        Set<Long> likeIdSet = new HashSet<>();
        if (likeIds != null && !likeIds.isEmpty()) {
            Arrays.stream(likeIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .forEach(likeIdSet::add);
        }
        film.setLikes(likeIdSet);

        return film;
    }
}