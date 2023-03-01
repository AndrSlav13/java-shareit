package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public enum ItemRequestDTO {
    ;

    private interface Description {
        @NotNull
        String getDescription();
    }

    public enum Controller {
        ;

        @Builder
        @Data
        public static class NewItemRequestDTO implements Description {
            String description;
            String ihatetests;    //Если оставить только description, то почему-то не происходит десериализация в объект
            //Т.е. пришлось вставить ненужный параметр. Также помогает убрать @Builder, но не хочется
        }

        @Builder
        @Data
        public static class ReturnItemRequestDTO {
            Long id;
            String description;
            Long requestorId;
            String created;
            List<ItemDTO.Controller.ReturnItemDTO> items;
        }

        public static class Mapper {
            public static ReturnItemRequestDTO toDTO(ItemRequest request) {
                ReturnItemRequestDTO item = ReturnItemRequestDTO.builder()
                        .id(request.getId())
                        .description(request.getDescription())
                        .requestorId(request.getRequestor().getId())
                        .created(request.getCreated().format(BookingDTO.format))
                        .items(request.getItems().stream()
                                .map(it -> ItemDTO.Controller.Mapper.toReturnItemDTO(it, request.getId()))
                                .collect(Collectors.toList()))
                        .build();

                return item;
            }

            public static ItemRequest toItemRequest(NewItemRequestDTO requestDTO) {
                ItemRequest item = ItemRequest.builder()
                        .id(null)
                        .description(requestDTO.description)
                        .created(LocalDateTime.now())
                        .requestor(null)
                        .items(new ArrayList<>())
                        .build();

                return item;
            }
        }
    }
}
