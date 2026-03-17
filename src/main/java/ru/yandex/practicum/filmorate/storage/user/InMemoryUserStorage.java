package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user.getName());
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Обновлён пользователь с id={}", newUser.getId());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public Collection<User> getUsers() {
        return users.values();
    }

    @Override
    public User getById(Long id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryUserStorage.");
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryUserStorage.");
    }

    @Override
    public Set<Long> getFriendsIds(Long userId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryUserStorage.");
    }

    @Override
    public List<User> getUsersByIds(Collection<Long> userIds) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryUserStorage.");
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherId) {
        throw new UnsupportedOperationException("Не поддерживается в InMemoryUserStorage.");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
