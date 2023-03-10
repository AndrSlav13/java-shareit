package ru.practicum.shareit.request.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String description;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", referencedColumnName = "id")
    private User requestor;

    @ToString.Exclude
    @ManyToMany(mappedBy = "requests", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @Column(name = "created")
    private LocalDateTime created;

    public void addRecommendedItems(Item item) {
        items.add(item);
        item.addRequests(this);
    }

    public void removeRecommendedItems(Item item) {
        items.remove(item);
        item.removeRequests(this);
    }
}
