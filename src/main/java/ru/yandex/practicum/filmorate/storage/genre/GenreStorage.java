package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenreStorage {
    List<Genre> findAll();

    Genre findById(int id);

    List<Genre> getGenresByIds(Set<Integer> genreIds);
}
