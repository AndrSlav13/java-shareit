package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

public enum ItemRequestDTO { ;

    @Data
    @Builder
    public static class NewReturnItemRequestDTO {
        private Integer id;
        private String description;
        private Integer requestorId;
        private String creationDate;
    }

    public static class Mapper {
        public static NewReturnItemRequestDTO ToDTO(ItemRequest request) {
            NewReturnItemRequestDTO item = NewReturnItemRequestDTO.builder()
                    .id(request.getId())
                    .description(request.getDescription())
                    .requestorId(request.getRequestorId())
                    .build();

            return item;
        }

        public static ItemRequest ToItemRequest(NewReturnItemRequestDTO requestDTO) {
            ItemRequest item = ItemRequest.builder()
                    .id(requestDTO.getId())
                    .description(requestDTO.getDescription())
                    .requestorId(requestDTO.getRequestorId())
                    .creationDate(requestDTO.getCreationDate())
                    .build();

            return item;
        }
    }
}
