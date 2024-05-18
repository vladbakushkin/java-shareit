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
    public ItemDetailsDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @Validated @RequestBody ItemRequestDto itemRequestDto) {
        return itemService.addItem(userId, itemRequestDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDetailsDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                     @Validated @RequestBody ItemUpdateDto itemUpdateDto) {
        return itemService.updateItem(userId, itemId, itemUpdateDto);
    }

    @GetMapping("/{itemId}")
    public ItemDetailsDto getItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                  @PathVariable Long itemId) {
        return itemService.getItem(userId, itemId);
    }

    @GetMapping
    public List<ItemDetailsDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByOwner(userId);
    }

    @GetMapping("/search")
    public List<ItemDetailsDto> searchItem(@RequestParam String text) {
        return itemService.searchAvailableItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentResponseDto addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @PathVariable Long itemId,
                                         @Validated @RequestBody CommentRequestDto commentRequestDto) {
        return itemService.addComment(userId, itemId, commentRequestDto);
    }
}
