package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.BookingDtoMapper;
import ru.practicum.shareit.utility.CommentDtoMapper;
import ru.practicum.shareit.utility.ItemDtoMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDetailsDto addItem(Long userId, ItemRequestDto itemRequestDto) {
        Item item = ItemDtoMapper.toItem(itemRequestDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        item.setUser(owner);
        Item savedItem = itemRepository.save(item);
        return ItemDtoMapper.toItemDetailsDto(savedItem);
    }

    @Override
    public ItemDetailsDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        Item item = ItemDtoMapper.toItem(itemUpdateDto);

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
        return ItemDtoMapper.toItemDetailsDto(updatedItem);
    }

    @Override
    public ItemDetailsDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));

        List<Booking> bookingsForItems = bookingRepository.findAllByItemIdOrderByEndDesc(item.getId());

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        return makeItemWithBookingsAndComments(userId, item, bookingsForItems, comments);
    }

    @Override
    public List<ItemDetailsDto> getAllItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByUserId(userId);

        List<Booking> bookingsForItems = bookingRepository.findAllByItemInOrderByEndDesc(items);

        List<Comment> comments = commentRepository.findAllByItemIn(items);

        return items.stream()
                .map(i -> this.makeItemWithBookingsAndComments(userId, i, bookingsForItems, comments))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDetailsDto> searchAvailableItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableItem(text).stream()
                .map(ItemDtoMapper::toItemDetailsDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentResponseDto addComment(Long userId, Long itemId, CommentRequestDto commentRequestDto) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));

        List<Booking> bookingsForItem = bookingRepository.findAllByItemIdOrderByEndDesc(item.getId());

        bookingsForItem.stream()
                .filter(b -> Objects.equals(b.getBooker().getId(), author.getId()))
                .filter(b -> b.getEnd().isBefore(LocalDateTime.now()))
                .findFirst()
                .orElseThrow(() -> new BadRequestException("User with id = " + userId +
                        " not booking the item id = " + item.getId()));

        Comment comment = CommentDtoMapper.toComment(commentRequestDto);
        comment.setAuthor(author);
        comment.setItem(item);
        Comment savedComment = commentRepository.save(comment);
        return CommentDtoMapper.toResponseDto(savedComment);
    }

    private ItemDetailsDto makeItemWithBookingsAndComments(Long userId, Item item, List<Booking> bookings,
                                                           List<Comment> comments) {
        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> last = bookings.stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(b -> Objects.equals(b.getItem().getUser().getId(), userId))
                .filter(b -> Objects.equals(b.getItem().getId(), item.getId()))
                .filter(b -> b.getStart().isBefore(now))
                .findFirst();


        List<Booking> bookingsForItemOrderByStartAsc = bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart))
                .collect(Collectors.toList());

        Optional<Booking> next = bookingsForItemOrderByStartAsc.stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(b -> Objects.equals(b.getItem().getUser().getId(), userId))
                .filter(b -> Objects.equals(b.getItem().getId(), item.getId()))
                .filter(b -> b.getStart().isAfter(now) || b.getStart().isEqual(now))
                .findFirst();

        ItemDetailsDto dto = ItemDtoMapper.toItemDetailsDto(item);
        last.ifPresent(booking -> dto.setLastBooking(BookingDtoMapper.toBookingRequestDto(last.get())));
        next.ifPresent(booking -> dto.setNextBooking(BookingDtoMapper.toBookingRequestDto(next.get())));
        if (comments != null) {
            dto.setComments(comments.stream()
                    .map(CommentDtoMapper::toResponseDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
