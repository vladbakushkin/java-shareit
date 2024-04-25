package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.NewUserDtoMapper;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDtoMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        NewUserDto newUserDto = createNewUserDto();

        // when
        when(userService.saveUser(any(NewUserDto.class))).thenReturn(newUserDto);

        // then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUserDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void updateUser_RequestIsValid_ReturnUser() throws Exception {
        // given
        UpdateUserDto updateUserDto = createUpdateUserDto();

        // when
        when(userService.updateUser(any(Long.class), any(UpdateUserDto.class))).thenReturn(updateUserDto);

        // then
        mockMvc.perform(
                        patch("/users/" + updateUserDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void getUser() throws Exception {
        // given
        NewUserDto userToGet = createNewUserDto();

        // when
        when(userService.getUser(any(Long.class))).thenReturn(userToGet);

        // then
        mockMvc.perform(
                        get("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("User"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void getAllUsers() throws Exception {
        // given
        NewUserDto newUserToGet1 = createNewUserDto();
        NewUserDto newUserToGet2 = createNewUserDto();
        newUserToGet2.setEmail("user2@email.com");

        // when
        when(userService.getAllUsers()).thenReturn(List.of(newUserToGet1, newUserToGet2));

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

    private NewUserDto createNewUserDto() {
        NewUserDtoMapper newUserDtoMapper = new NewUserDtoMapper();
        User user = new User();
        user.setName("User");
        user.setEmail("user@email.com");
        return newUserDtoMapper.toDto(user);
    }

    private UpdateUserDto createUpdateUserDto() {
        UpdateUserDtoMapper updateUserDtoMapper = new UpdateUserDtoMapper();
        User user = new User();
        user.setId(1L);
        user.setName("New Name");
        user.setEmail("user@email.com");
        return updateUserDtoMapper.toDto(user);
    }
}