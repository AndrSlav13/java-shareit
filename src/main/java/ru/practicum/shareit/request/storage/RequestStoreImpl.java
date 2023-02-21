package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class RequestStoreImpl implements RequestStore {
    private static final AtomicLong count = new AtomicLong(1L);  //Счетчик id
    private HashMap<Long, ItemRequestDTO.NewReturnItemRequestDTO> requestStore;

    public RequestStoreImpl() {
        requestStore = new HashMap<>();
    }

    @Override
    public Long addRequest(ItemRequestDTO.NewReturnItemRequestDTO requestDTO) {
        Long cnt = count.getAndIncrement();
        requestDTO.setId(cnt);
        requestStore.put(cnt, requestDTO);
        return cnt;
    }
}
