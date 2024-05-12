package ru.practicum.shareit.booking.dto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class BookingDtoMapper {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

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

    public Booking toBooking(Long userId, BookingRequestDto bookingRequestDto) {

        if (bookingRequestDto.getStart().isEqual(bookingRequestDto.getEnd())) {
            throw new BadRequestException("Start time must not be equal end time");
        }
        if (bookingRequestDto.getStart().isAfter(bookingRequestDto.getEnd())) {
            throw new BadRequestException("End time must be after start time");
        }

        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item with id = " + bookingRequestDto.getItemId() + " not found"));

        User booker = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id = " + userId + " not found"));

        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.valueOf(bookingRequestDto.getStatus().name()))
                .build();
    }
}
