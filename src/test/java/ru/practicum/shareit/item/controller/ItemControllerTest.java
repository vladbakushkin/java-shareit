package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemListingDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
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
        ItemDto itemDto = createItemDto();

        // when
        when(itemService.addItem(any(Long.class), any(ItemDto.class))).thenReturn(itemDto);

        // then
        mockMvc.perform(
                        post("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Item"))
                .andExpect(jsonPath("$.description").value("Description"))
                .andExpect(jsonPath("$.available").value("true"));
    }

    @Test
    void updateItem_RequestIsValid_ReturnItem() throws Exception {
        // given
        UpdateItemDto updateItemDto = createUpdateItemDto();

        // when
        when(itemService.updateItem(any(Long.class), any(Long.class), any(UpdateItemDto.class))).thenReturn(updateItemDto);

        // then
        mockMvc.perform(
                        patch("/items/" + updateItemDto.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(updateItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Updated Item"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.available").value("false"));
    }

    @Test
    void getItem() throws Exception {
        // given
        ItemListingDto itemDto = createItemListingDto();

        // when
        when(itemService.getItem(any(Long.class), any(Long.class))).thenReturn(itemDto);

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
    void getAllItemsByOwner() throws Exception {
        // given
        ItemListingDto itemDto1 = createItemListingDto();
        ItemListingDto itemDto2 = createItemListingDto();
        itemDto2.setName("Item2");
        itemDto2.setDescription("Description2");
        itemDto2.setAvailable(false);

        // when
        when(itemService.getAllItemsByOwner(any(Long.class))).thenReturn(List.of(itemDto1, itemDto2));

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
        ItemDto itemDto1 = createItemDto();
        ItemDto itemDto2 = createItemDto();
        itemDto2.setName("Item2");
        itemDto2.setDescription("Description2");
        itemDto2.setAvailable(false);

        // when
        when(itemService.searchAvailableItem(any(String.class))).thenReturn(List.of(itemDto1, itemDto2));

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

    private ItemDto createItemDto() {
        return ItemDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }

    private UpdateItemDto createUpdateItemDto() {
        return UpdateItemDto.builder()
                .id(1L)
                .name("Updated Item")
                .description("Updated Description")
                .available(false)
                .build();
    }

    private ItemListingDto createItemListingDto() {
        return ItemListingDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }
}