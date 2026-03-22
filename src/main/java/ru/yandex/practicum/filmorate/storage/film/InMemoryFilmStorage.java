package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.*;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film.getName());
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());

            oldFilm.setName(newFilm.getName());
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Обновлён фильм с id={}", oldFilm.getId());
            return oldFilm;
        }
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Collection<Film> getFilms() {
        return films.values();
    }

    @Override
    public Film getById(Long id) {
        return Optional.ofNullable(films.get(id))
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public void saveGenres(Film film) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryFilmStorage.");
    }

    public Set<Genre> getGenres(long filmId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryFilmStorage.");
    }

    @Override
    public Set<Long> getLikes(long filmId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryFilmStorage.");
    }

    @Override
    public void addLike(long filmId, long userId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryFilmStorage.");
    }

    @Override
    public void removeLike(long filmId, long userId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryFilmStorage.");
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryFilmStorage.");
    }

    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;

    }
}
