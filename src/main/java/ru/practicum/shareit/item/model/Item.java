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
@Builder(toBuilder = true)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;  //user id

    @ToString.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "requests_items_ids",
            joinColumns = @JoinColumn(name = "item_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "request_id", referencedColumnName = "id"))
    private List<ItemRequest> requests = new ArrayList<>();  //request id

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

    public void addRequests(ItemRequest request) {
        requests.add(request);
    }

    public void removeRequests(ItemRequest request) {
        requests.remove(request);
    }
}