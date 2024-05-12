package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDtoMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;

    private final BookingDtoMapper bookingDtoMapper;
    private final UserRepository userRepository;

    @Override
    public BookingResponseDto addBooking(Long userId, BookingRequestDto bookingRequestDto) {
        Booking booking = bookingDtoMapper.toBooking(userId, bookingRequestDto);

        if (Objects.equals(userId, booking.getItem().getUser().getId())) {
            throw new NotFoundException("Unable to book your item");
        }

        if (!booking.getItem().getAvailable()) {
            throw new BadRequestException("Item with id = " + booking.getItem().getId() + " unavailable");
        }

        Booking savedBooking = bookingRepository.save(booking);
        return bookingDtoMapper.toDto(savedBooking);
    }

    @Override
    public BookingResponseDto handleBooking(Long userId, Long bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking with id = " + bookingId + " not found"));

        if (!Objects.equals(userId, booking.getItem().getUser().getId())) {
            throw new NotFoundException("User with id = " + userId + " not owner");
        }

        if (approved) {
            if (booking.getStatus() == BookingStatus.APPROVED) {
                throw new BadRequestException("Booking with id = " + bookingId + " is already approved");
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        Booking updatedBooking = bookingRepository.save(booking);
        return bookingDtoMapper.toDto(updatedBooking);
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

        return bookingDtoMapper.toDto(booking);
    }

    @Override
    public List<BookingResponseDto> getAllBookingsForUser(Long userId, String state) {
        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        List<Booking> bookingsForUser;
        switch (state) {
            case "ALL":
                bookingsForUser = bookingRepository.findAllByBookerIdOrderByStartDesc(booker.getId());
                break;
            case "CURRENT":
                // где старт тайм < текущего времени
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStartBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now());
                break;
            case "PAST":
                // где енд тайм < текущего времени
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndEndBeforeOrderByStartDesc(booker.getId(), LocalDateTime.now());
                break;
            case "FUTURE":
                // где старт тайм > текущего времени
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStartAfterOrderByStartDesc(booker.getId(), LocalDateTime.now());
                break;
            case "WAITING":
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingsForUser = bookingRepository
                        .findAllByBookerIdAndStatusOrderByStartDesc(booker.getId(), BookingStatus.REJECTED);
                break;
            default:
                throw new UnknownStateException(state);
        }

        return bookingsForUser.stream().map(bookingDtoMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingResponseDto> getAllBookingsForUserItems(Long userId, String state) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        List<Booking> bookingsForUserItems;
        switch (state) {
            case "ALL":
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsStateAllOrderByStartDesc(user.getId());
                break;
            case "CURRENT":
                // где старт тайм < текущего времени
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsStateCurrentOrderByStartDesc(user.getId(), LocalDateTime.now());
                break;
            case "PAST":
                // где енд тайм < текущего времени
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsStatePastOrderByStartDesc(user.getId(), LocalDateTime.now());
                break;
            case "FUTURE":
                // где старт тайм > текущего времени
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsStateFutureOrderByStartDesc(user.getId(), LocalDateTime.now());
                break;
            case "WAITING":
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsByStatusOrderByStartDesc(user.getId(), BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingsForUserItems = bookingRepository
                        .findAllBookingsForUserItemsByStatusOrderByStartDesc(user.getId(), BookingStatus.REJECTED);
                break;
            default:
                throw new UnknownStateException(state);
        }

        return bookingsForUserItems.stream().map(bookingDtoMapper::toDto).collect(Collectors.toList());
    }
}
