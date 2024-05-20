package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.utility.CustomHeaders;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookingResponseDto addBooking(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                         @Validated @RequestBody BookingRequestDto bookingDto) {
        return bookingService.addBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingResponseDto handleBooking(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam boolean approved) {
        return bookingService.handleBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingResponseDto getBooking(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                         @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingResponseDto> getAllBookingsForUser(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                                          @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsForUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getAllBookingsForUserItems(@RequestHeader(CustomHeaders.X_SHARER_USER_ID) Long userId,
                                                               @RequestParam(defaultValue = "ALL") BookingState state) {
        return bookingService.getAllBookingsForUserItems(userId, state);
    }
}
