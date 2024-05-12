package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {

    @Null
    private Long id;

    @FutureOrPresent
    @NotNull
    private LocalDateTime start;

    @Future
    @NotNull
    private LocalDateTime end;

    @NotNull
    private Item item;

    @NotNull
    private User booker;

    private BookingStatus status = BookingStatus.WAITING;
}
