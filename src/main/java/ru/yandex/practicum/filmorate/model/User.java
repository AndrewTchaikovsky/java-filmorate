package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class User {
    Long id;
    String email;
    String login;
    String name;
    LocalDate birthday;
}
