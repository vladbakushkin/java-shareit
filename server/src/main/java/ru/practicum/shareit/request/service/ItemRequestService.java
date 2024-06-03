package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestResponseDto addRequest(Long userId, ItemRequestRequestDto requestDto);

    List<ItemRequestResponseDto> getMyRequests(Long userId);

    List<ItemRequestResponseDto> getAllRequests(Long userId, Integer from, Integer size);

    ItemRequestResponseDto getRequest(Long userId, Long requestId);
}
