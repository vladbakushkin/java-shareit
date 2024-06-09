package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    UserResponseDto saveUser(UserRequestDto userRequestDto);

    UserResponseDto updateUser(Long userId, UserRequestDto userRequestDto);

    UserResponseDto getUser(Long id);

    List<UserResponseDto> getAllUsers(Integer from, Integer size);

    void deleteUser(Long id);
}
