package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

public enum ItemRequestDTO {
    ;

    @Data
    @Builder
    public static class NewReturnItemRequestDTO {
        private Long id;
        private String description;
        private Long requestorId;
        private String creationDate;
    }

    public static class Mapper {
        public static NewReturnItemRequestDTO toDTO(ItemRequest request) {
            NewReturnItemRequestDTO item = NewReturnItemRequestDTO.builder()
                    .id(request.getId())
                    .description(request.getDescription())
                    .requestorId(request.getRequestor().getId())
                    .build();

            return item;
        }

        public static ItemRequest toItemRequest(NewReturnItemRequestDTO requestDTO) {
            ItemRequest item = ItemRequest.builder()
                    .id(requestDTO.getId())
                    .description(requestDTO.getDescription())
                    .creationDate(requestDTO.getCreationDate())
                    .build();

            return item;
        }
    }
}
