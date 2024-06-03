package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import java.util.List;

public interface UserService {
    UserResponseDto saveUser(UserNewDto userNewDto);

    UserResponseDto updateUser(Long userId, UserUpdateDto userUpdateDto);

    UserResponseDto getUser(Long id);

    List<UserResponseDto> getAllUsers(Integer from, Integer size);

    void deleteUser(Long id);
}
