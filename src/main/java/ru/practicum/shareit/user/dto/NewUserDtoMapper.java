package ru.practicum.shareit.user.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

@Component
public class NewUserDtoMapper {

    public NewUserDto toDto(User user) {
        NewUserDto newUserDto = new NewUserDto();
        newUserDto.setId(user.getId());
        newUserDto.setName(user.getName());
        newUserDto.setEmail(user.getEmail());

        return newUserDto;
    }

    public User toUser(NewUserDto newUserDto) {
        User user = new User();
        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());
        return user;
    }
}
