package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {
    ItemDetailsDto addItem(Long userId, ItemRequestDto itemRequestDto);

    ItemDetailsDto updateItem(Long userId, Long itemId, ItemRequestDto itemRequestDto);

    ItemDetailsDto getItem(Long userId, Long itemId);

    List<ItemDetailsDto> getAllItemsByOwner(Long userId, Integer from, Integer size);

    List<ItemDetailsDto> searchAvailableItem(String text, Integer from, Integer size);

    CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}
