package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Comment;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum CommentDTO {
    ;
    public static final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    //Проверки для данных загружаемых в слой сервиса из контроллера и БД

    private interface Text {
        @NotNull
        String getText();
    }


    public enum Controller {
        ;

        @Builder
        @Data
        public static class NewCommentDTO implements Text {
            String text;
            String notused;  //С одним полем дто не работает. Пришлось добавить это не нужное.
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