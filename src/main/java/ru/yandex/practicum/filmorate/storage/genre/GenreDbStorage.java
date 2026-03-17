package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.mappers.GenreRowMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class GenreDbStorage implements GenreStorage {
    private final GenreRowMapper genreRowMapper = new GenreRowMapper();
    private static final String FIND_ALL_QUERY = "SELECT * FROM genres ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String GET_GENRES_BY_IDS = "SELECT * FROM genres WHERE id IN (%s)";
    private final JdbcTemplate jdbc;

    public GenreDbStorage(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL_QUERY, genreRowMapper);
    }

    @Override
    public Genre findById(int id) {
        List<Genre> result = jdbc.query(FIND_BY_ID_QUERY, genreRowMapper, id);
        if (result.isEmpty()) {
            throw new NotFoundException("Жанр с id " + id + " не найден.");
        }
        return result.get(0);
    }

    public Set<Genre> getGenresByIds(Set<Integer> genreIds) {
        String placeholder = genreIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));
        String query = String.format(GET_GENRES_BY_IDS, placeholder);
        return new HashSet<>(jdbc.query(query, genreRowMapper, genreIds.toArray()));
    }
}
