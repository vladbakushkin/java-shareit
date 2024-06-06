package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.utility.CustomHeaders;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestResponseDto addRequest(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                             @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        return itemRequestService.addRequest(userId, itemRequestRequestDto);
    }

    @GetMapping
    public List<ItemRequestResponseDto> getMyRequests(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId) {
        return itemRequestService.getMyRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestResponseDto> getAllRequests(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                                       @RequestParam(defaultValue = "0") Integer from,
                                                       @RequestParam(defaultValue = "10") Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ItemRequestResponseDto getRequest(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long requestId) {
        return itemRequestService.getRequest(userId, requestId);
    }
}
