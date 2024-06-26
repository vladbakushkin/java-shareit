package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.dto.UserNewDto;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient itemClient;

    @SneakyThrows
    @Test
    void addItem_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("item");
        itemRequestDto.setDescription("description");
        itemRequestDto.setAvailable(true);
        when(itemClient.addItem(1L, itemRequestDto))
                .thenReturn(new ResponseEntity<>(itemRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        post("/items")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @SneakyThrows
    @Test
    void addItem_nameIsBlank_ThrowsBadRequestException() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("");
        itemRequestDto.setDescription("Description");
        itemRequestDto.setAvailable(true);

        // then
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"name\" Причина: \"must not be blank\""));
    }

    @SneakyThrows
    @Test
    void addItem_descriptionIsBlank_ThrowsBadRequestException() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("name");
        itemRequestDto.setDescription("");
        itemRequestDto.setAvailable(true);

        // then
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"description\" Причина: \"must not be blank\""));
    }

    @Test
    void addItem_availableIsNull_ThrowsBadRequestException() throws Exception {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("name");
        itemRequestDto.setDescription("description");
        itemRequestDto.setAvailable(null);

        // then
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"available\" Причина: \"must not be null\""));
    }

    @SneakyThrows
    @Test
    void updateItem_IsValid_ReturnsResponseEntity() {
        // given
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("item");
        itemUpdateDto.setDescription("description");
        when(itemClient.updateItem(1L, 1L, itemUpdateDto))
                .thenReturn(new ResponseEntity<>(itemUpdateDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        patch("/items/{itemId}", 1L)
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @SneakyThrows
    @Test
    void getItem_IsValid_ReturnsResponseEntity() {
        // given
        ItemUpdateDto itemUpdateDto = new ItemUpdateDto();
        itemUpdateDto.setName("item");
        itemUpdateDto.setDescription("description");
        when(itemClient.getItem(1L, 1L))
                .thenReturn(new ResponseEntity<>(itemUpdateDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/items/{itemId}", 1L)
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("item"))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @SneakyThrows
    @Test
    void addComment_IsValid_ReturnsResponseEntity() {
        // given
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setText("comment");
        when(itemClient.addComment(1L, 1L, commentRequestDto))
                .thenReturn(new ResponseEntity<>(commentRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        post("/items/{itemId}/comment", 1L)
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("comment"));
    }

    @SneakyThrows
    @Test
    void addComment_textIsBlank_ThrowsBadRequestException() {
        // given
        CommentRequestDto commentRequestDto = new CommentRequestDto();
        commentRequestDto.setItem(new ItemRequestDto());
        commentRequestDto.setText("");
        commentRequestDto.setAuthor(new UserNewDto());

        // then
        mockMvc.perform(
                        post("/items/1/comment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"text\" Причина: \"must not be blank\""));
    }

    @SneakyThrows
    @Test
    void getAllItemsByOwner_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("name");
        itemRequestDto.setDescription("description");
        itemRequestDto.setAvailable(true);

        when(itemClient.getAllItemsByOwner(1L, 0, 10))
                .thenReturn(new ResponseEntity<>(itemRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/items")
                                .header("X-SHARER-USER-ID", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @SneakyThrows
    @Test
    void getAllItemsByOwner_pageArgumentsWrong_ThrowsConstraintViolationException() {
        // then
        mockMvc.perform(
                        get("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-SHARER-USER-ID", "1")
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllItemsByOwner.from\" " +
                        "Причина: \"must be greater than or equal to 0\""));

        mockMvc.perform(
                        get("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-SHARER-USER-ID", "1")
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"getAllItemsByOwner.size\" " +
                        "Причина: \"must be greater than 0\""));
    }

    @SneakyThrows
    @Test
    void searchItem_IsValid_ReturnsResponseEntity() {
        // given
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setName("name");
        itemRequestDto.setDescription("item");
        itemRequestDto.setAvailable(true);

        when(itemClient.searchAvailableItem("iTe", 0, 10))
                .thenReturn(new ResponseEntity<>(itemRequestDto, HttpStatus.OK));

        // then
        mockMvc.perform(
                        get("/items/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-SHARER-USER-ID", "1")
                                .param("text", "iTe")
                                .param("from", "0")
                                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("item"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @SneakyThrows
    @Test
    void searchItem_pageArgumentsWrong_ThrowsConstraintViolationException() {
        // then
        mockMvc.perform(
                        get("/items/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-SHARER-USER-ID", "1")
                                .param("text", "text")
                                .param("from", String.valueOf(-1))
                                .param("size", String.valueOf(10)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"searchItem.from\" " +
                        "Причина: \"must be greater than or equal to 0\""));

        mockMvc.perform(
                        get("/items/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-SHARER-USER-ID", "1")
                                .param("text", "text")
                                .param("from", String.valueOf(0))
                                .param("size", String.valueOf(0)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Неправильно составлен запрос. " +
                        "Поле: \"searchItem.size\" " +
                        "Причина: \"must be greater than 0\""));
    }
}