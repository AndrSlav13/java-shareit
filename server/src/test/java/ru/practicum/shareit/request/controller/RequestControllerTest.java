package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class RequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestService requestService;

    @Autowired
    private MockMvc mvc;

    private ItemRequestDTO.Controller.NewItemRequestDTO requestDTO;

    @Test
    void addRequest() throws Exception {
        requestDTO = ItemRequestDTO.Controller.NewItemRequestDTO.builder()
                .description(request.getDescription())
                .build();
        Mockito
                .when(requestService.addRequest(Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    ItemRequest b = (ItemRequest) (i.getArguments()[0]);
                    b = b.toBuilder().requestor(user).items(List.of(item)).build();
                    return ItemRequestDTO.Controller.Mapper.toDTO(b);
                });

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(requestDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(request.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestorId").value(request.getRequestor().getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.items[0].id").value(item.getId()));
    }

    @Test
    void getRequestsByRequestorTest() throws Exception {
        Mockito
                .when(requestService.getRequestsByRequestorId(Mockito.any()))
                .thenAnswer(i -> {
                    user = user.toBuilder().id((Long) (i.getArguments()[0])).build();
                    request = request.toBuilder().requestor(user).build();
                    return List.of(ItemRequestDTO.Controller.Mapper.toDTO(request));
                });

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requestorId").value(99L));
    }

    @Test
    void getRequestsByNotRequestorTest() throws Exception {
        Mockito
                .when(requestService.getRequestsByNotRequestorId(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    user = user.toBuilder().id((Long) (i.getArguments()[0])).build();
                    request = request.toBuilder().requestor(user).build();
                    return List.of(ItemRequestDTO.Controller.Mapper.toDTO(request));
                });

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].requestorId").value(99L));
    }

    @Test
    void getRequestByIdTest() throws Exception {
        Mockito
                .when(requestService.getItemRequest(Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    user = user.toBuilder().id((Long) (i.getArguments()[0])).build();
                    request = request.toBuilder().requestor(user).build();
                    return ItemRequestDTO.Controller.Mapper.toDTO(request);
                });

        mvc.perform(get("/requests/{id}", request.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 99L))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.requestorId").value(99L));

    }


    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String itemName = "Щетка";
    String itemDescription = "something";
    Boolean itemAvailable = true;
    Long userId = 2L;
    Long itemId = 25L;
    Long requestId = 88L;
    String requestDescription = "req desc";
    LocalDateTime requestCreated = LocalDateTime.now();

    User user = null;
    Item item = null;
    ItemRequest request = null;


    @BeforeEach
    public void before() {
        user = User.builder()
                .id(userId)
                .items(new ArrayList<>())
                .bookings(new ArrayList<>())
                .requests(new ArrayList<>())
                .email(userEmail)
                .name(userName)
                .build();
        item = Item.builder()
                .id(itemId)
                .requests(new ArrayList<>())
                .bookings(new ArrayList<>())
                .comments(new ArrayList<>())
                .owner(user)
                .description(itemDescription)
                .name(itemName)
                .available(itemAvailable)
                .build();
        request = ItemRequest.builder()
                .items(new ArrayList<>())
                .requestor(user)
                .created(requestCreated)
                .id(requestId)
                .description(requestDescription)
                .build();

    }

}
