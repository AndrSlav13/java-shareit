package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
//Data for table
@Builder
@Data
public class Item {
    private Integer id;
    private String name;
    private String description;
    private Boolean available;
    private Integer ownerId;  //user id
    private Integer requestId;  //request id

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o || this.getId() == ((Item) o).getId()) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return this.id;
    }
}