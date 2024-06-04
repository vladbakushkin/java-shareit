package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class UserNewDto {

    @NotBlank
    @Email(message = "Email should be valid")
    private String email;

    private String name;
}
