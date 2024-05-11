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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
    private final UpdateItemDtoMapper updateItemDtoMapper = new UpdateItemDtoMapper();

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = itemDtoMapper.toItem(itemDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        item.setUser(owner);
        Item savedItem = itemRepository.save(item);
        return itemDtoMapper.toDto(savedItem);
    }

    @Override
    public UpdateItemDto updateItem(Long userId, Long itemId, UpdateItemDto updateItemDto) {
        Item item = updateItemDtoMapper.toItem(updateItemDto);

        Item itemToUpdate = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        if (!Objects.equals(owner.getId(), itemToUpdate.getUser().getId())) {
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

        Item updatedItem = itemRepository.save(itemToUpdate);
        return updateItemDtoMapper.toDto(updatedItem);
    }

    @Override
    public ItemDto getItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));
        return itemDtoMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAllItemsByOwner(Long userId) {
        return itemRepository.findAllByUserId(userId).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableItem(text).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }
}
