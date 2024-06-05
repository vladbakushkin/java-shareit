package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserRequestDto;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
    private ItemService itemService;

    @Test
    void addItem_RequestIsValid_ReturnItem() throws Exception {
        // given
        ItemDetailsDto itemDetailsDto = createItemDetailsDto();

        // when
        when(itemService.addItem(any(Long.class), any(ItemRequestDto.class))).thenReturn(itemDetailsDto);

        // then
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(itemDetailsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    void updateItem_RequestIsValid_ReturnItem() throws Exception {
        // given
        ItemDetailsDto itemDetailsDto = createItemDetailsDto();

        // when
        when(itemService.updateItem(any(Long.class), any(Long.class), any(ItemRequestDto.class)))
                .thenReturn(itemDetailsDto);

        // then
        mockMvc.perform(
                        patch("/items/" + itemDetailsDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(itemDetailsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    void getItem_valid_ReturnsItem() throws Exception {
        // given
        ItemDetailsDto itemDetailsDto = createItemDetailsDto();

        // when
        when(itemService.getItem(any(Long.class), any(Long.class))).thenReturn(itemDetailsDto);

        // then
        mockMvc.perform(
                        get("/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    void getItem_wrongItemId_ThrowsNotFoundException() throws Exception {
        // given
        long itemId = 99L;

        when(itemService.getItem(any(Long.class), any(Long.class))).thenThrow(NotFoundException.class);

        // then
        mockMvc.perform(
                        get("/items/{itemId}", itemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllItemsByOwner() throws Exception {
        // given
        ItemDetailsDto itemDetailsDto1 = createItemDetailsDto();
        ItemDetailsDto itemDetailsDto2 = createItemDetailsDto();
        itemDetailsDto2.setName("Item2");
        itemDetailsDto2.setDescription("Description2");
        itemDetailsDto2.setAvailable(false);

        // when
        when(itemService.getAllItemsByOwner(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemDetailsDto1, itemDetailsDto2));

        // then
        mockMvc.perform(
                        get("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[1].name").value("Item2"))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].available").value("false"));
    }

    @Test
    void searchItem() throws Exception {
        // given
        ItemDetailsDto itemDetailsDto1 = createItemDetailsDto();
        ItemDetailsDto itemDetailsDto2 = createItemDetailsDto();
        itemDetailsDto2.setName("Item2");
        itemDetailsDto2.setDescription("Description2");
        itemDetailsDto2.setAvailable(false);

        // when
        when(itemService.searchAvailableItem(any(String.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemDetailsDto1, itemDetailsDto2));

        // then
        mockMvc.perform(
                        get("/items/search")
                                .param("text", "scRipT")
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Item"))
                .andExpect(jsonPath("$[0].description").value("Description"))
                .andExpect(jsonPath("$[0].available").value("true"))
                .andExpect(jsonPath("$[1].name").value("Item2"))
                .andExpect(jsonPath("$[1].description").value("Description2"))
                .andExpect(jsonPath("$[1].available").value("false"));
    }

    @Test
    void addComment_requestIsValid_ReturnsComment() throws Exception {
        // given
        CommentRequestDto commentRequestDto = createCommentRequestDto();
        commentRequestDto.setText("text");
        UserRequestDto author = new UserRequestDto();
        author.setName("name");
        author.setName("email");
        commentRequestDto.setAuthor(author);

        CommentResponseDto commentResponseDto = new CommentResponseDto();
        commentResponseDto.setId(1L);
        commentResponseDto.setAuthorName(commentRequestDto.getAuthor().getName());
        commentResponseDto.setText(commentRequestDto.getText());

        // when
        when(itemService.addComment(anyLong(), anyLong(), any(CommentRequestDto.class)))
                .thenReturn(commentResponseDto);

        // then
        mockMvc.perform(
                        post("/items/1/comment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(commentResponseDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentResponseDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentResponseDto.getCreated()));
    }

    private ItemDetailsDto createItemDetailsDto() {
        return ItemDetailsDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }

    private CommentRequestDto createCommentRequestDto() {
        return CommentRequestDto.builder()
                .item(new ItemRequestDto())
                .build();
    }
}