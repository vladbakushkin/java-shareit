package ru.practicum.shareit.item.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Comment;

import java.time.LocalDateTime;

@Component
public class CommentDtoMapper {

    public CommentResponseDto toResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(LocalDateTime.now())
                .build();
    }

    public Comment toComment(CommentRequestDto dto) {
        return Comment.builder()
                .text(dto.getText())
                .build();
    }
}
