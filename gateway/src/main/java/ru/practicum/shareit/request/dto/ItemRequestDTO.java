package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.ItemDTO;

import javax.validation.constraints.NotNull;
import java.util.List;

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
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NewItemRequestDTO implements Description {
            String description;
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

    }
}
