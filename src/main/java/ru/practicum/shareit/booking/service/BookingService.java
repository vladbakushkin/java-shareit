package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {

    BookingResponseDto addBooking(Long userId, BookingRequestDto bookingDto);

    BookingResponseDto handleBooking(Long userId, Long bookingId, boolean approved);

    BookingResponseDto getBooking(Long userId, Long bookingId);

    List<BookingResponseDto> getAllBookings(Long userId, BookingState state, String path,
                                            Integer from, Integer size);
}
