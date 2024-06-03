package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;

@Data
public class UserUpdateDto {

    private Long id;

    @Email
    private String email;

    private String name;
}