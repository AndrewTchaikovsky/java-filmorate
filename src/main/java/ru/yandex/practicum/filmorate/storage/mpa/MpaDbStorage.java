package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mappers.MpaRowMapper;

import java.util.List;

@Repository
public class MpaDbStorage implements MpaStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM mpa_ratings";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";
    private final JdbcTemplate jdbc;

    public MpaDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Mpa> findAll() {
        return jdbc.query(FIND_ALL_QUERY, new MpaRowMapper());
    }

    @Override
    public Mpa findById(int id) {
        List<Mpa> result = jdbc.query(FIND_BY_ID_QUERY, new MpaRowMapper(), id);
        if (result.isEmpty()) {
            throw new NotFoundException("Рейтинг с id " + id + " не найден.");
        }
        return result.get(0);
    }
}
