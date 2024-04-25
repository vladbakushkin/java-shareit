package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    UpdateItemDto updateItem(Long userId, Long itemId, UpdateItemDto updateItemDto);

    ItemDto getItem(Long itemId);

    List<ItemDto> getAllItemsByOwner(Long userId);

    List<ItemDto> searchAvailableItem(String text);
}
