package ru.practicum.shareit.item.storage;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("select comment " +
            "from Comment as comment " +
            "where comment.itemCommented.id in :ids " +
            "order by comment.created desc ")
    List<Comment> findComments(@Param("ids") List<Long> itemIds, Pageable pag);
}