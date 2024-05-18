package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
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
    private final CommentRepository commentRepository;

    private final ItemDtoMapper itemDtoMapper = new ItemDtoMapper();
    private final BookingDtoMapper bookingDtoMapper = new BookingDtoMapper();
    private final CommentDtoMapper commentDtoMapper = new CommentDtoMapper();

    @Override
    public ItemDetailsDto addItem(Long userId, ItemRequestDto itemRequestDto) {
        Item item = itemDtoMapper.toItem(itemRequestDto);

        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        item.setUser(owner);
        Item savedItem = itemRepository.save(item);
        return itemDtoMapper.toItemDetailsDto(savedItem);
    }

    @Override
    public ItemDetailsDto updateItem(Long userId, Long itemId, ItemUpdateDto itemUpdateDto) {
        Item item = itemDtoMapper.toItem(itemUpdateDto);

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
        return itemDtoMapper.toItemDetailsDto(updatedItem);
    }

    @Override
    public ItemDetailsDto getItem(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item with id = " + itemId + " not found"));
        return makeItemWithBookingsAndComments(userId, item);
    }

    @Override
    public List<ItemDetailsDto> getAllItemsByOwner(Long userId) {
        List<Item> items = itemRepository.findAllByUserId(userId);
        return items.stream()
                .map(i -> this.makeItemWithBookingsAndComments(userId, i))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDetailsDto> searchAvailableItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchAvailableItem(text).stream()
                .map(itemDtoMapper::toItemDetailsDto)
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

        Comment comment = commentDtoMapper.toComment(commentRequestDto);
        comment.setAuthor(author);
        comment.setItem(item);
        Comment savedComment = commentRepository.save(comment);
        return commentDtoMapper.toResponseDto(savedComment);
    }

    private ItemDetailsDto makeItemWithBookingsAndComments(Long userId, Item item) {
        List<Booking> bookingsForItemOrderByEndDesc = bookingRepository.findAllByItemIdOrderByEndDesc(item.getId());
        List<Booking> bookingsForItemOrderByStartAsc = bookingRepository.findAllByItemIdOrderByStartAsc(item.getId());

        LocalDateTime now = LocalDateTime.now();

        Optional<Booking> last = bookingsForItemOrderByEndDesc.stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(b -> Objects.equals(b.getItem().getUser().getId(), userId))
                .filter(b -> b.getStart().isBefore(now))
                .findFirst();

        Optional<Booking> next = bookingsForItemOrderByStartAsc.stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED))
                .filter(b -> Objects.equals(b.getItem().getUser().getId(), userId))
                .filter(b -> b.getStart().isAfter(now) || b.getStart().isEqual(now))
                .findFirst();

        ItemDetailsDto dto = itemDtoMapper.toItemDetailsDto(item);

        last.ifPresent(booking -> dto.setLastBooking(bookingDtoMapper.toBookingRequestDto(last.get())));
        next.ifPresent(booking -> dto.setNextBooking(bookingDtoMapper.toBookingRequestDto(next.get())));

        List<Comment> comments = commentRepository.findAllByItemId(item.getId());

        if (comments != null) {
            dto.setComments(comments.stream().map(commentDtoMapper::toResponseDto).collect(Collectors.toList()));
        }

        return dto;
    }
}
