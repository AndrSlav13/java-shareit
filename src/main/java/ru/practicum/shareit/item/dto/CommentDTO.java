package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceSort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public enum CommentDTO {
    ;
    public static final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    //Проверки для данных загружаемых в слой сервиса из контроллера и БД
    private interface Id {
        @NotNull
        Long getId();
    }

    private interface Name {
        @NotNull
        @NotBlank
        String getName();
    }

    private interface Text {
        @NotNull
        String getText();
    }


    public enum Controller {
        ;

        @Data
        public static class NewCommentDTO implements Text{
            String text;
        }

        @Builder
        @Data
        public static class ReturnCommentDTO {
            Long id;
            String text;
            String authorName;
            LocalDateTime created;
        }


        public static class Mapper {

            public static Comment toComment(NewCommentDTO dtoComment) {
                Comment comment = Comment.builder()
                        .text(dtoComment.text)
                        .id(null)   //Set in DB context
                        .authorName("unknown")
                        .created(LocalDateTime.now())
                        .build();

                return comment;
            }

            public static ReturnCommentDTO toReturnCommentDTO(Comment comment) {
                ReturnCommentDTO dtoComment = ReturnCommentDTO.builder()
                        .id(comment.getId())
                        .authorName(comment.getAuthorName())
                        .text(comment.getText())
                        .created(comment.getCreated())
                        .build();

                return dtoComment;
            }
        }
    }

}