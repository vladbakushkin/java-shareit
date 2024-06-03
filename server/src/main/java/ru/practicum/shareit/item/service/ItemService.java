package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDetailsDto addItem(Long userId, ItemRequestDto itemRequestDto);

    ItemDetailsDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto);

    ItemDetailsDto getItem(Long userId, Long itemId);

    List<ItemDetailsDto> getAllItemsByOwner(Long userId, Integer from, Integer size);

    List<ItemDetailsDto> searchAvailableItem(String text, Integer from, Integer size);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
