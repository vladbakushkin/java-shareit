package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    User save(User user);

    User update(Long userId, User user);

    User findUserById(Long userId);

    List<User> findAll();

    void deleteUserById(Long userId);
}
