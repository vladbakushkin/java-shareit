package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMapper;
import ru.practicum.shareit.item.dto.UpdateItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDtoMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    private final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
    private final UpdateItemDtoMapper updateItemDtoMapper = new UpdateItemDtoMapper();

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = itemDtoMapper.toItem(itemDto);

        NewUserDto owner = userService.getUser(userId);
        if (owner == null) {
            throw new NotFoundException("User with id = " + userId + " not found");
        }
        item.setOwnerId(userId);
        Item savedItem = itemRepository.save(userId, item);
        return itemDtoMapper.toDto(savedItem);
    }

    @Override
    public UpdateItemDto updateItem(Long userId, Long itemId, UpdateItemDto updateItemDto) {
        Item item = updateItemDtoMapper.toItem(updateItemDto);

        Item itemToUpdate = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id = " + itemId + " not found");
        }

        item.setId(itemId);
        NewUserDto owner = userService.getUser(userId);
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

        Item updatedItem = itemRepository.update(userId, itemId, itemToUpdate);
        return updateItemDtoMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findItemById(itemId);
        if (item == null) {
            throw new NotFoundException("Item with id = " + itemId + " not found");
        }
        return itemDtoMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        return itemRepository.findAllItemsByUserId(userId).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        return itemRepository.searchAvailableItem(text).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
