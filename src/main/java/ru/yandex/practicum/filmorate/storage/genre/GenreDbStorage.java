package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.List;

@Repository
public class GenreDbStorage implements GenreStorage {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private final JdbcTemplate jdbc;

    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, new GenreRowMapper());
    }

    @Override
    public Genre findById(int id) {
        List<Genre> result = jdbc.query(FIND_BY_ID_QUERY, new GenreRowMapper(), id);
        if (result.isEmpty()) {
            throw new NotFoundException("Жанр с id " + id + " не найден.");
        }
        return result.get(0);
    }
}
