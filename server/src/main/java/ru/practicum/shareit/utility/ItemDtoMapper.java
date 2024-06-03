package ru.practicum.shareit.utility;

import ru.practicum.shareit.item.dto.ItemDetailsDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.item.model.Item;

public final class ItemDtoMapper {

    private ItemDtoMapper() {
    }

    public static ItemDetailsDto toItemDetailsDto(Item item) {
        return ItemDetailsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequestId())
                .build();
    }

    public static Item toItem(ItemRequestDto itemRequestDto) {
        return Item.builder()
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .build();
    }

    public static Item toItem(ItemUpdateDto itemUpdateDto) {
        return Item.builder()
                .name(itemUpdateDto.getName())
                .description(itemUpdateDto.getDescription())
                .available(itemUpdateDto.getAvailable())
                .build();
    }
}
