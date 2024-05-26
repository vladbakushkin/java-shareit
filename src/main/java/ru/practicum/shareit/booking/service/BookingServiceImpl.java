package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utility.BookingDtoMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto addBooking(Long userId, BookingRequestDto bookingRequestDto) {
        Booking booking = BookingDtoMapper.toBooking(bookingRequestDto);

        if (booking.getStart().isEqual(booking.getEnd())) {
            throw new BadRequestException("Start time must not be equal end time");
        }
        if (booking.getStart().isAfter(booking.getEnd())) {
            throw new BadRequestException("End time must be after start time");
        }

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id = " + bookingRequestDto.getItemId() + " not found"));
        booking.setItem(item);

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));
        booking.setBooker(booker);

        if (Objects.equals(userId, booking.getItem().getUser().getId())) {
            throw new NotFoundException("Unable to book your item");
        }

        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item with id = " + booking.getItem().getId() + " unavailable");
        }

        Booking savedBooking = bookingRepository.save(booking);
        return BookingDtoMapper.toDto(savedBooking);
    }

    @Override
    public BookingResponseDto handleBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id = " + bookingId + " not found"));

        if (!Objects.equals(userId, booking.getItem().getUser().getId())) {
            throw new NotFoundException("User with id = " + userId + " not owner");
        }

        if (approved && booking.getStatus() == BookingStatus.APPROVED) {
            throw new BadRequestException("Booking with id = " + bookingId + " is already approved");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);

        Booking updatedBooking = bookingRepository.save(booking);
        return BookingDtoMapper.toDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id = " + bookingId + " not found"));

        if (!Objects.equals(userId, booking.getBooker().getId()) &&
                !Objects.equals(userId, booking.getItem().getUser().getId())) {
            log.debug("userId = {}, bookingId = {}, bookerId = {}, ownerId = {}",
                    userId, bookingId, booking.getBooker().getId(), booking.getItem().getUser().getId());
            throw new NotFoundException("User with id = " + userId + " is not owner of booking or item");
        }

        return BookingDtoMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsForUser(Long userId, BookingState state, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        if (from < 0 || size <= 0) {
            throw new BadRequestException("'size' must be > 0 and 'from' must be >= 0. " +
                    "size = " + size + ", from = " + from);
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<Booking> bookingsForUser;

        switch (state) {
            case ALL:
                bookingsForUser = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId(), pageable);
                break;
            case CURRENT:
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStartBeforeAndEndAfterOrderByStartDesc(booker.getId(), now, now, pageable);
                break;
            case PAST:
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), now, pageable);
                break;
            case FUTURE:
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), now, pageable);
                break;
            case WAITING:
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnknownStateException(state.name());
        }

        return bookingsForUser.stream().map(BookingDtoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsForUserItems(Long userId, BookingState state, Integer from, Integer size) {
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        if (from < 0 || size <= 0) {
            throw new BadRequestException("'size' must be > 0 and 'from' must be >= 0. " +
                    "size = " + size + ", from = " + from);
        }

        int page = from / size;
        Pageable pageable = PageRequest.of(page, size);

        List<Booking> bookingsForUserItems;
        switch (state) {
            case ALL:
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsOrderByStartDesc(user.getId(), pageable);
                break;
            case CURRENT:
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsWhereStartBeforeAndEndAfterOrderByStartDesc(user.getId(), now, now, pageable);
                break;
            case PAST:
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsWhereEndBeforeOrderByStartDesc(user.getId(), now, pageable);
                break;
            case FUTURE:
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsWhereStartAfterOrderByStartDesc(user.getId(), now, pageable);
                break;
            case WAITING:
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsByStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING, pageable);
                break;
            case REJECTED:
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsByStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED, pageable);
                break;
            default:
                throw new UnknownStateException(state.name());
        }

        return bookingsForUserItems.stream().map(BookingDtoMapper::toDto).collect(Collectors.toList());
    }
}
