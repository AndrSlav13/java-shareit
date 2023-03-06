package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public enum BookingDTO {
    ;
    public static final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTime stringToLocalDateTime(String str) {
        LocalDateTime dateTime = LocalDateTime.parse(str, format);
        return dateTime;
    }

    public static Instant stringToInstant(String str) {
        return stringToLocalDateTime(str).atZone(ZoneId.systemDefault()).toInstant();
    }

    //Проверки для данных загружаемых в слой сервиса из контроллера и БД
    private interface Start {
        @NotNull
        String getStart();
    }

    private interface End {
        @NotNull
        String getEnd();
    }

    private interface Status {
        @EnumStatusBookingConstrain
        String getStatus();
    }

    public enum Controller {
        ;

        @Builder
        @Data
        @DateTimeConstrain(start = "start", end = "end")
        public static class NewBookItemDTO implements Start, End, Status {
            String start;
            String end;
            Long itemId;
            Long bookerId;
            String status;
        }

        @Builder
        @Data
        public static class ReturnBookItemDTO {
            Long id;
            String start;
            String end;
            ItemDTO.Controller.ReturnItemDTO item;
            UserDTO.Controller.ReturnUserDTO booker;
            String status;
        }

        @Builder
        @Data
        public static class ReturnBookItemSimpleDTO {
            Long id;
            Long bookerId;
        }

    }
}