package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final ItemDtoMapper itemDtoMapper;
    private final UpdateItemDtoMapper updateItemDtoMapper;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @Validated @RequestBody ItemDto itemDto) {
        Item item = itemDtoMapper.toItem(itemDto);
        Item savedItem = itemService.addItem(userId, item);
        return itemDtoMapper.toDto(savedItem);
    }

    @PatchMapping("/{itemId}")
    public UpdateItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @Validated @RequestBody UpdateItemDto updateItemDto) {
        Item item = updateItemDtoMapper.toItem(updateItemDto);
        Item updatedItem = itemService.updateItem(userId, itemId, item);
        return updateItemDtoMapper.toDto(updatedItem);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemDtoMapper.toDto(itemService.getItem(itemId));
    }

    @GetMapping
    public List<ItemDto> getAllItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAllItemsByOwner(userId).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam String text) {
        return itemService.searchAvailableItem(text).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
