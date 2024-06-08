package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserNewDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

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
    private UserClient userClient;

    @Test
    void createUser_IsValid_ReturnsResponseEntity() throws Exception {
        // given
        UserNewDto userNewDto = new UserNewDto();
        userNewDto.setName("user");
        userNewDto.setEmail("user@email.com");
        when(userClient.createUser(userNewDto))
                .thenReturn(new ResponseEntity<>(userNewDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userNewDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void createUser_EmailInvalid_ThrowsMethodArgumentNotValidException() throws Exception {
        // given
        UserNewDto userNewDto = new UserNewDto();
        userNewDto.setName("name");
        userNewDto.setEmail("user.email.com");

        // then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userNewDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"email\" Причина: \"Email should be valid\""));
    }

    @Test
    void updateUser_IsValid_ReturnsResponseEntity() throws Exception {
        // given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("user");
        userUpdateDto.setEmail("user@email.com");
        when(userClient.updateUser(1L, userUpdateDto))
                .thenReturn(new ResponseEntity<>(userUpdateDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        patch("/users/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void getUser_IsValid_ReturnsResponseEntity() throws Exception {
        // given
        UserUpdateDto userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("user");
        userUpdateDto.setEmail("user@email.com");
        when(userClient.getUser(1L))
                .thenReturn(new ResponseEntity<>(userUpdateDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/users/{userId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("user"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void getAllUsers_IsValid_ReturnsResponseEntity() throws Exception {
        // given
        UserNewDto userNewDto = new UserNewDto();
        userNewDto.setName("name");
        userNewDto.setEmail("user@email.com");
        when(userClient.getAllUsers(0, 10))
                .thenReturn(new ResponseEntity<>(userNewDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.email").value("user@email.com"));
    }

    @Test
    void getAllUsers_pageArgumentsWrong_ThrowsConstraintViolationException() throws Exception {
        // then
        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllUsers.from\" " +
                        "Причина: \"must be greater than or equal to 0\""));

        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllUsers.size\" " +
                        "Причина: \"must be greater than 0\""));
    }

    @Test
    void deleteUser_IsValid_ReturnsResponseEntity() throws Exception {
        // given
        when(userClient.deleteUser(1L))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        // then
        mockMvc.perform(
                        delete("/users/1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}