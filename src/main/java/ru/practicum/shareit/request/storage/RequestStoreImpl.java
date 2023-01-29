package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.dto.ItemRequestDTO;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class RequestStoreImpl implements RequestStore {
    private static final AtomicInteger count = new AtomicInteger(1);  //Счетчик id
    private HashMap<Integer, ItemRequestDTO.NewReturnItemRequestDTO> requestStore;

    public RequestStoreImpl() {
        requestStore = new HashMap<>();
    }

    @Override
    public Integer addRequest(ItemRequestDTO.NewReturnItemRequestDTO requestDTO) {
        Integer cnt = count.getAndIncrement();
        requestDTO.setId(cnt);
        requestStore.put(cnt, requestDTO);
        return cnt;
    }
}
