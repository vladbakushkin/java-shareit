package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    private final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
    private final UpdateItemDtoMapper updateItemDtoMapper = new UpdateItemDtoMapper();
    private final BookingDtoMapper bookingDtoMapper = new BookingDtoMapper();

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
    public ItemListingDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));
        return makeItemWithBookings(userId, item);
    }

    @Override
    public List<ItemListingDto> getAllItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByUserId(userId);
        return items.stream()
                .map(i -> this.makeItemWithBookings(userId, i))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchAvailableItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        //
        return itemRepository.searchAvailableItem(text).stream()
                .map(itemDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private ItemListingDto makeItemWithBookings(Long userId, Item item) {
        List<Booking> bookingsForItemOrderByEndDesc = bookingRepository.findAllByItemIdOrderByEndDesc(item.getId());
        List<Booking> bookingsForItemOrderByStartAsc = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());

        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> last = bookingsForItemOrderByEndDesc.stream()
                .filter(b -> Objects.equals(b.getItem().getUser().getId(), userId))
                .filter(b -> b.getEnd().isBefore(now) || b.getEnd().isEqual(now))
                .findFirst();

        Optional<Booking> next = bookingsForItemOrderByStartAsc.stream()
                .filter(b -> Objects.equals(b.getItem().getUser().getId(), userId))
                .filter(b -> b.getStart().isAfter(now) || b.getStart().isEqual(now))
                .findFirst();

        ItemListingDto dto = itemDtoMapper.toItemListingDto(item);

        last.ifPresent(booking -> dto.setLastBooking(bookingDtoMapper.toBookingRequestDto(last.get())));
        next.ifPresent(booking -> dto.setNextBooking(bookingDtoMapper.toBookingRequestDto(next.get())));
        return dto;
    }
}
