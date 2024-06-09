package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void saveUser_RequestIsValid_ReturnUser() throws Exception {
        // given
        UserResponseDto userResponseDto = createUserResponseDto();

        // when
        when(userService.saveUser(any(UserRequestDto.class))).thenReturn(userResponseDto);

        // then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userResponseDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void updateUser_RequestIsValid_ReturnUser() throws Exception {
        // given
        UserResponseDto userResponseDto = createUserResponseDto();

        // when
        when(userService.updateUser(any(Long.class), any(UserRequestDto.class))).thenReturn(userResponseDto);

        // then
        mockMvc.perform(
                        patch("/users/" + userResponseDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void getUser_valid_ReturnsUserDto() throws Exception {
        // given
        UserResponseDto userResponseDto = createUserResponseDto();

        // when
        when(userService.getUser(any(Long.class))).thenReturn(userResponseDto);

        // then
        mockMvc.perform(
                        get("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void getUser_wrongUserId_ThrowsNotFoundException() throws Exception {
        // given
        long userId = 99L;

        // when
        when(userService.getUser(anyLong())).thenThrow(NotFoundException.class);

        // then
        mockMvc.perform(
                        get("/users/{userId}", userId)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers() throws Exception {
        // given
        UserResponseDto userResponseDto1 = createUserResponseDto();
        UserResponseDto userResponseDto2 = createUserResponseDto();
        userResponseDto2.setEmail("user2@email.com");

        // when
        when(userService.getAllUsers(any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(userResponseDto1, userResponseDto2));

        // then
        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("User"))
                .andExpect(jsonPath("$[0].email").value("user@email.com"))
                .andExpect(jsonPath("$[1].name").value("User"))
                .andExpect(jsonPath("$[1].email").value("user2@email.com"));
    }

    @Test
    void deleteUser() throws Exception {
        // given
        doNothing().when(userService).deleteUser(any(Long.class));

        // then
        mockMvc.perform(
                        delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    private UserResponseDto createUserResponseDto() {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(1L);
        userResponseDto.setName("User");
        userResponseDto.setEmail("user@email.com");
        return userResponseDto;
    }
}