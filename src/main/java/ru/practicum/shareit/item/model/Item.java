package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
//Data for table
@Builder
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;  //user id

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest request;  //request id

    @ToString.Exclude
    @OneToMany(mappedBy = "booked", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "itemCommented", fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setBooked(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setBooked(null);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setItemCommented(this);
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setItemCommented(null);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != this.getClass()) return false;
        if (this == o || this.getId().equals(((Item) o).getId())) return true;
        return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}