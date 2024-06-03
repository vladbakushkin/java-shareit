package ru.practicum.shareit.utility;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

public final class CommentDtoMapper {

    private CommentDtoMapper() {
    }

    public static CommentResponseDto toResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(LocalDateTime.now())
                .build();
    }

    public static Comment toComment(CommentRequestDto dto) {
        return Comment.builder()
                .text(dto.getText())
                .build();
    }
}
