package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;

@Component
@RequiredArgsConstructor
public class BookingDtoMapper {

    public BookingResponseDto toDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public BookingRequestDto toBookingRequestDto(Booking booking) {
        return BookingRequestDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .bookerId(booking.getBooker().getId())
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(BookingRequestDto bookingRequestDto) {

        if (bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new BadRequestException("Start time must not be equal end time");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new BadRequestException("End time must be after start time");
        }

        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .status(BookingStatus.valueOf(bookingRequestDto.getStatus().name()))
                .build();
    }
}
