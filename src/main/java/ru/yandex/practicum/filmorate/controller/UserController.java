package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.warn("Ошибка валидации пользователя: Электронная почта не может быть пустой и должна содержать символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.warn("Ошибка валидации пользователя: Логин не может быть пустым и содержать пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Ошибка валидации пользователя: Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user.getName());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User newUser) {
        if (newUser.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (users.containsKey(newUser.getId())) {
            User oldUser = users.get(newUser.getId());
            if (newUser.getEmail() == null || newUser.getEmail().isBlank() || !newUser.getEmail().contains("@")) {
                log.warn("Ошибка валидации пользователя: Электронная почта не может быть пустой и должна содержать символ @");
                throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
            }
            if (newUser.getLogin() == null || newUser.getLogin().isBlank() || newUser.getLogin().contains(" ")) {
                log.warn("Ошибка валидации пользователя: Логин не может быть пустым и содержать пробелы");
                throw new ValidationException("Логин не может быть пустым и содержать пробелы");
            }
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                newUser.setName(newUser.getLogin());
            }
            if (newUser.getBirthday() == null || newUser.getBirthday().isAfter(LocalDate.now())) {
                log.warn("Ошибка валидации пользователя: Дата рождения не может быть в будущем");
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Обновлён пользователь с id={}", newUser.getId());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @GetMapping
    public Collection<User> getUsers() {
        return users.values();
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






