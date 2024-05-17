package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemListingDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;

    private BookingRequestDto lastBooking;
    private BookingRequestDto nextBooking;

    private List<CommentResponseDto> comments;
}

