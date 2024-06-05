package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.utility.CustomHeaders;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                             @Valid @RequestBody ItemRequestRequestDto itemRequestRequestDto) {
        log.info("Adding new request {}", itemRequestRequestDto);
        return itemRequestClient.addRequest(userId, itemRequestRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getMyRequests(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId) {
        log.info("Getting my requests. userId={}", userId);
        return itemRequestClient.getMyRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all requests");
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                             @PathVariable Long requestId) {
        log.info("Getting request {}", requestId);
        return itemRequestClient.getRequest(userId, requestId);
    }
}
