package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NewUserDto saveUser(@Validated @RequestBody NewUserDto newUserDto) {
        return userService.saveUser(newUserDto);
    }

    @PatchMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateUserDto updateUser(@PathVariable Long userId,
                                    @Validated @RequestBody UpdateUserDto updateUserDto) {
        return userService.updateUser(userId, updateUserDto);
    }

    @GetMapping("/{userId}")
    public NewUserDto getUser(@PathVariable Long userId) {
        return userService.getUser(userId);
    }

    @GetMapping
    public List<NewUserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }
}
