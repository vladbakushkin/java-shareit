package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

@Component
public class UpdateItemDtoMapper {

    public UpdateItemDto toDto(Item item) {
        return UpdateItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public Item toItem(UpdateItemDto updateItemDto) {
        return Item.builder()
                .name(updateItemDto.getName())
                .description(updateItemDto.getDescription())
                .available(updateItemDto.getAvailable())
                .build();
    }
}
