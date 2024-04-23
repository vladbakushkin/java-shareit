package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item addItem(Long userId, Item item);

    Item updateItem(Long userId, Long itemId, Item item);

    Item getItem(Long itemId);

    List<Item> getAllItemsByOwner(Long userId);

    List<Item> searchAvailableItem(String text);
}
