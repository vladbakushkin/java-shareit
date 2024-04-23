package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item save(Long userId, Item item);

    Item update(Long userId, Long itemId, Item item);

    Item findItemById(Long itemId);

    List<Item> findAllItemsByUserId(Long userId);

    List<Item> searchAvailableItem(String text);
}
