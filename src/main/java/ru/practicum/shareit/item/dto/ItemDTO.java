/*
https://habr.com/ru/post/513072/
Преобразование формата данных из формата в программе (Item) в формат для представления в контроллере и БД
**/
package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//enum т.к. внутренний класс не сможет быть иначе static (если class вместо static).
// А если не static, то надо создавать экземпляры внешних классов - enum здесь это namespace
//get - значит проверка только когда берется значение. Если не используется, то и не проверяется
public enum ItemDTO {
    ;
    public static final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    //Проверки для данных загружаемых в слой сервиса из контроллера и БД

    private interface Name {
        @NotNull
        @NotBlank
        String getName();
    }

    private interface Description {
        @NotNull
        String getDescription();
    }

    private interface Available {
        @NotNull
        Boolean getAvailable();
    }

    public enum Controller {
        ;

        @Builder
        @Data
        public static class NewItemDTO implements Name, Description, Available {
            String name;
            String description;
            Boolean available;
            Long requestId;
        }

        @Builder
        @Data
        public static class UpdateItemDTO {
            String name;
            String description;
            Boolean available;
        }

        @Builder
        @Getter
        @Setter
        public static class ReturnItemDTO {
            Long id;
            String name;
            String description;
            Boolean available;
            Long ownerId;
            Long requestId;

            public ReturnItemDTO(Long id, String name, String description, Boolean available, Long ownerId, Long requestId) {
                this.id = id;
                this.name = name;
                this.description = description;
                this.available = available;
                this.ownerId = ownerId;
                this.requestId = requestId;
            }
        }

        @Getter
        @Setter
        public static class ReturnItemWithBookingsDTO extends ReturnItemDTO {
            BookingDTO.Controller.ReturnBookItemSimpleDTO lastBooking;
            BookingDTO.Controller.ReturnBookItemSimpleDTO nextBooking;
            List<CommentDTO.Controller.ReturnCommentDTO> comments;

            @Builder(builderMethodName = "returnItemWithBookingsDTOBuilder")
            public ReturnItemWithBookingsDTO(Long id, String name, String description, Boolean available, Long ownerId, Long requestId,
                                             BookingDTO.Controller.ReturnBookItemSimpleDTO lastBooking,
                                             BookingDTO.Controller.ReturnBookItemSimpleDTO nextBooking,
                                             List<CommentDTO.Controller.ReturnCommentDTO> comments) {
                super(id, name, description, available, ownerId, requestId);
                this.lastBooking = lastBooking;
                this.nextBooking = nextBooking;
                this.comments = comments;

            }
        }

        public static class Mapper {

            public static Item toItem(NewItemDTO dtoItem) {
                Item item = Item.builder()
                        .name(dtoItem.name)
                        .description(dtoItem.description)
                        .id(null)   //Set in DB context
                        .available(dtoItem.available)
                        .owner(null)   //Set id from headers
                        .bookings(new ArrayList<>())
                        .comments(new ArrayList<>())
                        .requests(new ArrayList<>())
                        .build();

                return item;
            }

            public static Item toItem(UpdateItemDTO dtoItem) {
                Item item = Item.builder()
                        .name(dtoItem.name)
                        .description(dtoItem.description)
                        .id(null)   //Set in DB context
                        .available(dtoItem.available)
                        .owner(null)   //Set id from headers
                        .build();

                return item;
            }

            public static ReturnItemDTO toReturnItemDTO(Item item, Long itemRequestIdForWitchAdded) {
                ReturnItemDTO dtoItem = ReturnItemDTO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwner().getId())
                        .requestId(itemRequestIdForWitchAdded)
                        .build();

                return dtoItem;
            }

            public static ReturnItemDTO toReturnItemDTO(Item item) {
                ReturnItemDTO dtoItem = ReturnItemDTO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwner().getId())
                        .requestId(null)
                        .build();

                return dtoItem;
            }

            public static ReturnItemWithBookingsDTO toReturnItemWithBookingsDTO(Item item, Booking lastBooking, Booking nextBooking, List<Comment> comments) {
                ReturnItemWithBookingsDTO dtoItem = ReturnItemWithBookingsDTO.returnItemWithBookingsDTOBuilder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwner().getId())
                        .nextBooking(nextBooking != null ? BookingDTO.Controller.Mapper.toReturnBookItemSimpleDTO(nextBooking) : null)
                        .lastBooking(lastBooking != null ? BookingDTO.Controller.Mapper.toReturnBookItemSimpleDTO(lastBooking) : null)
                        .comments(comments != null ? comments.stream()
                                .map(com -> CommentDTO.Controller.Mapper.toReturnCommentDTO(com))
                                .collect(Collectors.toList()) : null)
                        .build();

                return dtoItem;
            }
        }
    }

}