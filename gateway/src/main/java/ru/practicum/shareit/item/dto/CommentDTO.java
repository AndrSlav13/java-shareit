package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NewCommentDTO implements Text {
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
    }

}