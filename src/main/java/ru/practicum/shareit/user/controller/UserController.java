package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.NewUserDtoMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final NewUserDtoMapper newUserDtoMapper;
    private final UpdateUserDtoMapper updateUserDtoMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserDto saveUser(@Validated @RequestBody NewUserDto newUserDto) {
        User user = newUserDtoMapper.toUser(newUserDto);
        User savedUser = userService.saveUser(user);
        return newUserDtoMapper.toDto(savedUser);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateUserDto updateUser(@PathVariable Long userId,
                                    @Validated @RequestBody UpdateUserDto userDto) {
        User user = updateUserDtoMapper.toUser(userDto);
        User updatedUser = userService.updateUser(userId, user);
        return updateUserDtoMapper.toDto(updatedUser);
    }

    @GetMapping("/{userId}")
    public NewUserDto getUser(@PathVariable Long userId) {
        return newUserDtoMapper.toDto(userService.getUser(userId));
    }

    @GetMapping
    public List<NewUserDto> getAllUsers() {
        return userService.getAllUsers().stream()
                .map(newUserDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
