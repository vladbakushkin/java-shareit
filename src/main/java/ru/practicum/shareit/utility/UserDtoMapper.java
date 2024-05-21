package ru.practicum.shareit.utility;

import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;
import ru.practicum.shareit.user.model.User;

public final class UserDtoMapper {

    private UserDtoMapper() {
    }

    public static UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User toUser(UserNewDto userNewDto) {
        return User.builder()
                .name(userNewDto.getName())
                .email(userNewDto.getEmail())
                .build();
    }

    public static User toUser(UserUpdateDto userUpdateDto) {
        return User.builder()
                .name(userUpdateDto.getName())
                .email(userUpdateDto.getEmail())
                .build();
    }
}
