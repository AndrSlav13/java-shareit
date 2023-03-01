package ru.practicum.shareit.item.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "commentary")
    private String text;
    @Column(name = "author_name")
    private String authorName;
    private LocalDateTime created;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item itemCommented;

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o || this.getId().equals(((Comment) o).getId())) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
