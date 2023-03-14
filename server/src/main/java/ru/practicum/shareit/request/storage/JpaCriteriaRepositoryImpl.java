package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.List;

public class JpaCriteriaRepositoryImpl implements JpaCriteriaRepository {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<ItemRequest> findAllByNotRequestorId(Long requestorId, Integer from, Integer size) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ItemRequest> cr = cb.createQuery(ItemRequest.class);

        Root<ItemRequest> root = cr.from(ItemRequest.class);
        Join<ItemRequest, User> joinItems = root.join("requestor");
        Path<Long> idUser = joinItems.get("id");
        cr.select(root);

        Predicate userPredicat = cb.notEqual(idUser, cb.literal(requestorId));
        TypedQuery<ItemRequest> typedQuery = entityManager.createQuery(cr.where(userPredicat));
        typedQuery.setFirstResult(from);
        typedQuery.setMaxResults(size);
        List<ItemRequest> req = typedQuery.getResultList();

        return req;
    }

    @Override
    public List<ItemRequest> findAllByRequestorId(Long requestorId) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ItemRequest> cr = cb.createQuery(ItemRequest.class);

        Root<ItemRequest> root = cr.from(ItemRequest.class);
        Join<ItemRequest, User> joinItems = root.join("requestor");
        Path<Long> idUser = joinItems.get("id");
        cr.select(root);

        Predicate userPredicat = cb.equal(idUser, cb.literal(requestorId));
        List<ItemRequest> req = entityManager.createQuery(cr.where(userPredicat)).getResultList();

        return req;
    }
}
