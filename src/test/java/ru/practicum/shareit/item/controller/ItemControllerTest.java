package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
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
        when(itemService.updateItem(any(Long.class), any(Long.class), any(ItemUpdateDto.class))).thenReturn(itemDetailsDto);

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
    void getItem() throws Exception {
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
    void getAllItemsByOwner() throws Exception {
        // given
        ItemDetailsDto itemDetailsDto1 = createItemDetailsDto();
        ItemDetailsDto itemDetailsDto2 = createItemDetailsDto();
        itemDetailsDto2.setName("Item2");
        itemDetailsDto2.setDescription("Description2");
        itemDetailsDto2.setAvailable(false);

        // when
        when(itemService.getAllItemsByOwner(any(Long.class))).thenReturn(List.of(itemDetailsDto1, itemDetailsDto2));

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
        when(itemService.searchAvailableItem(any(String.class))).thenReturn(List.of(itemDetailsDto1, itemDetailsDto2));

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

    private ItemDetailsDto createItemDetailsDto() {
        return ItemDetailsDto.builder()
                .id(1L)
                .name("Item")
                .description("Description")
                .available(true)
                .build();
    }
}