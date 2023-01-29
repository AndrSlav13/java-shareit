/*
https://habr.com/ru/post/513072/
Преобразование формата данных из формата в программе (Item) в формат для представления в контроллере и БД
**/
package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.Item;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.format.DateTimeFormatter;

//enum т.к. внутренний класс не сможет быть иначе static (если class вместо static).
// А если не static, то надо создавать экземпляры внешних классов - enum здесь это namespace
//get - значит проверка только когда берется значение. Если не используется, то и не проверяется
public enum ItemDTO {
    ;
    public static final DateTimeFormatter format = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    //Проверки для данных загружаемых в слой сервиса из контроллера и БД
    private interface Id {
        @NotNull
        Integer getId();
    }

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

        @Data
        public static class NewItemDTO implements Name, Description, Available {
            String name;
            String description;
            Boolean available;
        }

        @Data
        public static class UpdateItemDTO {
            String name;
            String description;
            Boolean available;
        }

        @Builder
        @Data
        public static class ReturnItemDTO implements Id, Name, Description, Available {
            Integer id;
            String name;
            String description;
            Boolean available;
            Integer ownerId;
            Integer requestId;
        }

        public static class Mapper {
            public static Item toItem(NewItemDTO dtoItem, Integer idOwner) {
                Item item = Item.builder()
                        .name(dtoItem.name)
                        .description(dtoItem.description)
                        .id(null)   //Set in DB context
                        .available(dtoItem.available)
                        .ownerId(idOwner)   //Set id from headers
                        .requestId(null)    //No request
                        .build();

                return item;
            }

            public static Item toItem(NewItemDTO dtoItem) {
                Item item = Item.builder()
                        .name(dtoItem.name)
                        .description(dtoItem.description)
                        .id(null)   //Set in DB context
                        .available(dtoItem.available)
                        .ownerId(null)   //Set id from headers
                        .requestId(null)    //No request
                        .build();

                return item;
            }

            public static Item toItem(UpdateItemDTO dtoItem) {
                Item item = Item.builder()
                        .name(dtoItem.name)
                        .description(dtoItem.description)
                        .id(null)   //Set in DB context
                        .available(dtoItem.available)
                        .ownerId(null)   //Set id from headers
                        .requestId(null)    //No request
                        .build();

                return item;
            }

            public static ReturnItemDTO toReturnItemDTO(Item item) {
                ReturnItemDTO dtoItem = ReturnItemDTO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwnerId())
                        .requestId(item.getRequestId())
                        .build();

                return dtoItem;
            }
        }
    }

    //Из/в базу данных?
    public enum Database {
        ;

        @Builder    //DB 1
        @Data
        public static class DBItemDTO implements Id, Name, Description {
            Integer id;
            String name;
            String description;
            Boolean available;
            Integer ownerId;
            Integer requestId;
        }

        public static class Mapper {
            public static Item toItem(DBItemDTO dtoItem) {
                Item item = Item.builder()
                        .id(dtoItem.id)
                        .name(dtoItem.name)
                        .description(dtoItem.description)
                        .available(dtoItem.available)
                        .ownerId(dtoItem.ownerId)
                        .requestId(dtoItem.requestId)
                        .build();

                return item;
            }

            public static DBItemDTO toDBItemDTO(Item item) {
                DBItemDTO dbItem = DBItemDTO.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .description(item.getDescription())
                        .available(item.getAvailable())
                        .ownerId(item.getOwnerId())
                        .requestId(item.getRequestId())
                        .build();

                return dbItem;
            }
        }
    }
}