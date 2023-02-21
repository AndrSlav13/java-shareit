package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select new ru.practicum.shareit.item.model.Comment(b.id, b.text, b.authorName, b.created, b.itemCommented) " +
            "from Comment as b left join Booking as bk on b.itemCommented.id = bk.booked.id "+
            "where bk.booker.id = ?1 and bk.booked.id = ?2 ")
    List<Comment> findComments(Long userId, Long itemId);
}