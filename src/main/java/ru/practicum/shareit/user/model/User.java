package ru.practicum.shareit.user.model;

import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Builder(toBuilder = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String name;
    @Column(nullable = false, unique = true)
    private String email;
    @ToString.Exclude
    @OneToMany(mappedBy = "owner", fetch = FetchType.LAZY)
    private List<Item> items = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "requestor", fetch = FetchType.LAZY)
    private List<ItemRequest> requests = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "booker", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    public void addItem(Item it) {
        items.add(it);
        it.setOwner(this);
    }

    public void removeItem(Item it) {
        items.remove(it);
        it.setOwner(null);
    }

    public void addBooking(Booking it) {
        bookings.add(it);
        it.setBooker(this);
    }

    public void removeBooking(Booking it) {
        bookings.remove(it);
        it.setBooker(null);
    }

    public void addRequest(ItemRequest it) {
        requests.add(it);
        it.setRequestor(this);
    }

    public void removeRequest(ItemRequest it) {
        requests.remove(it);
        it.setRequestor(null);
    }
}
