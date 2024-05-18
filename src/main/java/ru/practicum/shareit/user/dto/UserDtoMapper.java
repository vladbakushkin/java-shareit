package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class UserDtoMapper {

    public UserResponseDto toDto(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserNewDto userNewDto) {
        return User.builder()
                .name(userNewDto.getName())
                .email(userNewDto.getEmail())
                .build();
    }

    public User toUser(UserUpdateDto userUpdateDto) {
        return User.builder()
                .name(userUpdateDto.getName())
                .email(userUpdateDto.getEmail())
                .build();
    }
}
