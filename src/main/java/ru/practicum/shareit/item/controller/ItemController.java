package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Validated @RequestBody ItemDto itemDto) {
        return itemService.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public UpdateItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @Validated @RequestBody UpdateItemDto updateItemDto) {
        return itemService.updateItem(userId, itemId, updateItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemListingDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemListingDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchAvailableItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @Validated @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}
