package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.utility.CustomHeaders;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDetailsDto addItem(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                  @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.addItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDetailsDto updateItem(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                     @PathVariable Long itemId,
                                     @Validated @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ItemDetailsDto getItem(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                  @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDetailsDto> getAllItemsByOwner(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId) {
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDetailsDto> searchItem(@RequestParam String text) {
        return itemService.searchAvailableItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                         @PathVariable Long itemId,
                                         @Validated @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}
