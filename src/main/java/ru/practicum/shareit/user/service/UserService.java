package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;

import java.util.List;

public interface UserService {
    NewUserDto saveUser(NewUserDto newUserDto);

    UpdateUserDto updateUser(Long userId, UpdateUserDto updateUserDto);

    NewUserDto getUser(Long id);

    List<NewUserDto> getAllUsers();

    void deleteUser(Long id);
}
