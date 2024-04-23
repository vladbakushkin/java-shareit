package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item addItem(Long userId, Item item) {
        User owner = userService.getUser(userId);
        if (owner == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        item.setOwnerId(userId);
        return itemRepository.save(userId, item);
    }

    @Override
    public Item updateItem(Long userId, Long itemId, Item item) {
        Item itemToUpdate = getItem(itemId);
        item.setId(itemId);
        User owner = userService.getUser(userId);
        if (owner == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }

        if (!Objects.equals(owner.getId(), itemToUpdate.getOwnerId())) {
            throw new NotFoundException("User with id = " + userId + " not owned by itemId = " + itemToUpdate.getId());
        }

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }

        return itemRepository.update(userId, itemId, itemToUpdate);
    }

    @Override
    public Item getItem(Long itemId) {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id = " + itemId + " not found");
        }
        return item;
    }

    @Override
    public List<Item> getAllItemsByOwner(Long userId) {
        return itemRepository.findAllItemsByUserId(userId);
    }

    @Override
    public List<Item> searchAvailableItem(String text) {
        return itemRepository.searchAvailableItem(text);
    }
}
