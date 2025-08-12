package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.MpaRatingStorage;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRatingDbStorage implements MpaRatingStorage {

    private final JdbcTemplate jdbcTemplate;

    public MpaRatingDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<MpaRating> findAll() {
        String sql = "SELECT mpa_rating_id as id, name FROM mpa_ratings ORDER BY mpa_rating_id";
        return jdbcTemplate.query(sql, (rs, rowNum) ->
                new MpaRating(rs.getInt("id"), rs.getString("name")));
    }

    @Override
    public Optional<MpaRating> findById(int id) {
        String sql = "SELECT mpa_rating_id as id, name FROM mpa_ratings WHERE mpa_rating_id = ?";
        try {
            MpaRating mpaRating = jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                    new MpaRating(rs.getInt("id"), rs.getString("name")), id);
            return Optional.ofNullable(mpaRating);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}