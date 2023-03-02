package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
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

        public static class Mapper {
            public static Booking toBooking(NewBookItemDTO dtoItem) {
                Booking item = Booking.builder()
                        .id(null)
                        .start(LocalDateTime.parse(dtoItem.start, format))
                        .end(LocalDateTime.parse(dtoItem.end, format))
                        .status(dtoItem.status == null || !BookingStatus.contains(dtoItem.status) ? BookingStatus.WAITING : BookingStatus.valueOf(dtoItem.status))
                        .build();

                return item;
            }

            public static ReturnBookItemDTO toReturnBookItemDTO(Booking bItem) {
                ReturnBookItemDTO item = ReturnBookItemDTO.builder()
                        .id(bItem.getId())
                        .start(bItem.getStart().format(format))
                        .end(bItem.getEnd().format(format))
                        .item(ItemDTO.Controller.Mapper.toReturnItemDTO(bItem.getBooked()))
                        .booker(UserDTO.Controller.Mapper.toReturnUserDTO(bItem.getBooker()))
                        .status(bItem.getStatus().name())
                        .build();

                return item;
            }

            public static ReturnBookItemSimpleDTO toReturnBookItemSimpleDTO(Booking bItem) {
                ReturnBookItemSimpleDTO item = ReturnBookItemSimpleDTO.builder()
                        .id(bItem.getId())
                        .bookerId(bItem.getBooker().getId())
                        .build();

                return item;
            }
        }
    }
}