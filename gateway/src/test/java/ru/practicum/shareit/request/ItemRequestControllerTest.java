package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient itemRequestClient;

    @SneakyThrows
    @Test
    void addRequest_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto("description");
        when(itemRequestClient.addRequest(1L, itemRequestRequestDto))
                .thenReturn(new ResponseEntity<>(itemRequestRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        post("/requests")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequestRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("description"));
    }

    @SneakyThrows
    @Test
    void getMyRequests_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto("description");
        when(itemRequestClient.getMyRequests(1L))
                .thenReturn(new ResponseEntity<>(itemRequestRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/requests")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("description"));
    }

    @SneakyThrows
    @Test
    void getAllRequests_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto("description");
        when(itemRequestClient.getAllRequests(1L, 0, 10))
                .thenReturn(new ResponseEntity<>(itemRequestRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/requests/all")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("description"));
    }

    @SneakyThrows
    @Test
    void getRequest_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto("description");

        when(itemRequestClient.getRequest(1L, 1L))
                .thenReturn(new ResponseEntity<>(itemRequestRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/requests/{requestId}", 1L)
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("description"));
    }

    @SneakyThrows
    @Test
    void addRequest_DescriptionIsBlank_ThrowsBadRequestException() {
        // given
        ItemRequestRequestDto requestDto = new ItemRequestRequestDto();
        requestDto.setDescription("");

        // then
        mockMvc.perform(
                        post("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"description\" Причина: \"must not be blank\""));
    }

    @SneakyThrows
    @Test
    void getAllRequests_pageArgumentsWrong_ThrowsConstraintViolationException() {
        // then
        mockMvc.perform(
                        get("/requests/all")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllRequests.from\" " +
                        "Причина: \"must be greater than or equal to 0\""));

        mockMvc.perform(
                        get("/requests/all")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllRequests.size\" " +
                        "Причина: \"must be greater than 0\""));
    }
}