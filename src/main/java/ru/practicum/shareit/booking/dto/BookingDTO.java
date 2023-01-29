package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public enum BookingDTO {;
    public static final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalDateTime stringToLocalDateTime(String str) {
        LocalDateTime dateTime = LocalDateTime.parse(str, format);
        return dateTime;
    }

    public static Instant stringToInstant(String str) {
        return stringToLocalDateTime(str).atZone(ZoneId.systemDefault()).toInstant();
    }

    public static String localDateTimeToString(LocalDateTime loc) {
        return loc.format(format);
    }

    public static Long instantToLong(Instant ins) {
        return ins.toEpochMilli();
    }

    public static String instantToString(Instant ins) {
        return LocalDateTime.ofInstant(ins, ZoneId.systemDefault()).format(format);
    }

    //Проверки для данных загружаемых в слой сервиса из контроллера и БД
    private interface Id {
        @NotNull
        Integer getId();
    }

    private interface Start {
        @NotNull
        @DateTimeConstrain
        String getStart();
    }

    private interface End {
        @NotNull
        @DateTimeConstrain
        String getEnd();
    }

    private interface StartLong {
        @NotNull
        @DateTimeConstrainLong
        Long getStart();
    }

    private interface EndLong {
        @NotNull
        @DateTimeConstrainLong
        Long getEnd();
    }

    private interface Status {
        @NotNull
        @EnumStatusBookingConstrain
        String getStatus();
    }

    private interface ItemId {
        @NotNull
        Integer getItemId();
    }

    public enum Controller {;

        @Builder
        @Data
        public static class NewBookItemDTO implements Start, End, ItemId {
            String start;
            String end;
            Integer itemId;
            Integer bookerId;
            String status;
        }

        @Builder
        @Data
        public static class ReturnBookItemDTO implements Id, Start, End, Status {
            Integer id;
            String start;
            String end;
            Integer itemId;
            Integer bookerId;
            String status;
        }

        public static class Mapper {
            public static Booking toBooking(NewBookItemDTO dtoItem) {
                Booking item = Booking.builder()
                        .id(null)
                        .start(stringToInstant(dtoItem.start))
                        .end(stringToInstant(dtoItem.end))
                        .itemId(dtoItem.itemId)
                        .bookerId(dtoItem.bookerId)
                        .status(BookingStatus.valueOf(dtoItem.status))
                        .build();

                return item;
            }

            public static ReturnBookItemDTO toReturnBookItemDTO(Booking bItem) {
                ReturnBookItemDTO item = ReturnBookItemDTO.builder()
                        .id(bItem.getId())
                        .start(instantToString(bItem.getStart()))
                        .end(instantToString(bItem.getEnd()))
                        .itemId(bItem.getItemId())
                        .bookerId(bItem.getBookerId())
                        .status(bItem.getStatus().name())
                        .build();

                return item;
            }
        }
    }

    //Из/в базу данных?
    public enum Database {;

        @Builder    //DB 2
        @Data
        public static class DBScheduleDTO implements Id, StartLong, EndLong, Status {
            Integer id;
            Long start;
            Long end;
            Integer itemId;
            Integer bookerId;
            String status;
        }

        public static class Mapper {
            public static DBScheduleDTO toDBScheduleDTO(Booking item) {
                DBScheduleDTO it = DBScheduleDTO.builder()
                        .id(item.getId())
                        .start(instantToLong(item.getStart()))
                        .end(instantToLong(item.getEnd()))
                        .itemId(item.getItemId())
                        .bookerId(item.getBookerId())
                        .status(item.getStatus().name())
                        .build();

                return it;
            }

            public static Booking toBooking(DBScheduleDTO item) {
                Booking it = Booking.builder()
                        .id(item.id)
                        .start(Instant.ofEpochMilli(item.start))
                        .end(Instant.ofEpochMilli(item.end))
                        .itemId(item.itemId)
                        .bookerId(item.bookerId)
                        .status(BookingStatus.valueOf(item.status))
                        .build();

                return it;
            }
        }
    }
}