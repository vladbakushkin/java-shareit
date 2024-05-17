package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    UpdateItemDto updateItem(Long userId, Long itemId, UpdateItemDto updateItemDto);

    ItemListingDto getItem(Long userId, Long itemId);

    List<ItemListingDto> getAllItemsByOwner(Long userId);

    List<ItemDto> searchAvailableItem(String text);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
