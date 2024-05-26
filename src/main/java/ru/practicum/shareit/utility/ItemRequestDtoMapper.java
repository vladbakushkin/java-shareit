package ru.practicum.shareit.utility;

import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

public final class ItemRequestDtoMapper {
    private ItemRequestDtoMapper() {
    }

    public static ItemRequestResponseDto toItemRequestResponseDto(final ItemRequest itemRequest) {
        return ItemRequestResponseDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .build();
    }

    public static ItemRequest toItemRequest(final ItemRequestRequestDto itemRequestRequestDto) {
        return ItemRequest.builder()
                .description(itemRequestRequestDto.getDescription())
                .build();
    }
}
