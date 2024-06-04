package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserNewDto;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    void saveUser_EmailInvalid_ThrowsMethodArgumentNotValidException() throws Exception {
        // given
        UserNewDto userNewDto = new UserNewDto();
        userNewDto.setName("name");
        userNewDto.setEmail("user.email.com");

        // then
        mockMvc.perform(
                        post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(userNewDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_pageArgumentsWrong_ThrowsConstraintViolationException() throws Exception {
        // then
        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(
                        get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }
}