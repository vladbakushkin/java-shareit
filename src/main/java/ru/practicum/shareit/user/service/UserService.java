package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);

    User updateUser(Long userId, User user);

    User getUser(Long id);

    List<User> getAllUsers();

    void deleteUser(Long id);
}
