package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Builder
public class ItemRequest {
    private Integer id;
    private String description;
    private Integer requestorId;
    private String creationDate;
}
