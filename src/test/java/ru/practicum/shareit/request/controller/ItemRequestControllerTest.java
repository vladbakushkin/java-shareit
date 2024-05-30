package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
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
    private ItemRequestService itemRequestService;

    private LocalDateTime created;

    @BeforeEach
    void setUp() {
        created = LocalDateTime.now();
    }

    @SneakyThrows
    @Test
    void addRequest_RequestIsValid_ReturnCorrectResponse() {
        // given
        ItemRequestRequestDto requestDto = getItemRequestRequestDto();
        ItemRequestResponseDto itemRequestResponseDto = getItemRequestResponseDto();
        when(itemRequestService.addRequest(any(Long.class), any())).thenReturn(itemRequestResponseDto);

        // then
        mockMvc.perform(
                        post("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void addRequest_DescriptionIsBlank_ThrowsBadRequestException() {
        // given
        ItemRequestRequestDto requestDto = getItemRequestRequestDto();
        requestDto.setDescription("");

        // then
        mockMvc.perform(
                        post("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getMyRequests_RequestIsValid_ReturnCorrectResponse() {
        // given
        ItemRequestResponseDto itemRequestResponseDto = getItemRequestResponseDto();
        when(itemRequestService.getMyRequests(any(Long.class)))
                .thenReturn(List.of(itemRequestResponseDto));

        // then
        mockMvc.perform(
                        get("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].created").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void getAllRequests_RequestIsValid_ReturnCorrectResponse() {
        // given
        ItemRequestResponseDto itemRequestResponseDto = getItemRequestResponseDto();
        when(itemRequestService.getAllRequests(any(Long.class), any(Integer.class), any(Integer.class)))
                .thenReturn(List.of(itemRequestResponseDto));

        // then
        mockMvc.perform(
                        get("/requests/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").isNotEmpty())
                .andExpect(jsonPath("$[0].description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$[0].created").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void getAllRequests_wrongParams_ThrowsBadRequestException() {
        // given
        when(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).thenThrow(BadRequestException.class);

        // then
        mockMvc.perform(
                        get("/requests/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1)
                                .param("from", String.valueOf(-1)))
                .andExpect(status().isBadRequest());
    }

    @SneakyThrows
    @Test
    void getRequest_RequestIsValid_ReturnCorrectResponse() {
        // given
        ItemRequestResponseDto itemRequestResponseDto = getItemRequestResponseDto();
        when(itemRequestService.getRequest(any(Long.class), any(Long.class))).thenReturn(itemRequestResponseDto);
        long requestId = 1L;

        // then
        mockMvc.perform(
                        get("/requests/{requestId}", requestId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description").value(itemRequestResponseDto.getDescription()))
                .andExpect(jsonPath("$.created").isNotEmpty());
    }

    @SneakyThrows
    @Test
    void getRequest_wrongRequestId_ThrowsNotFoundException() {
        // given
        long requestId = 99L;

        when(itemRequestService.getRequest(any(Long.class), any(Long.class))).thenThrow(NotFoundException.class);

        // then
        mockMvc.perform(
                        get("/requests/{requestId}", requestId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isNotFound());
    }

    private static ItemRequestRequestDto getItemRequestRequestDto() {
        ItemRequestRequestDto requestDto = new ItemRequestRequestDto();
        requestDto.setDescription("Description");
        return requestDto;
    }

    private ItemRequestResponseDto getItemRequestResponseDto() {
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto();
        itemRequestResponseDto.setId(1L);
        itemRequestResponseDto.setDescription("Description");
        itemRequestResponseDto.setCreated(created);
        return itemRequestResponseDto;
    }
}