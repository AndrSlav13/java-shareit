package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDTO.Controller.NewItemDTO itemDTO;

    @Test
    public void addItem() throws Exception {
        itemDTO = ItemDTO.Controller.NewItemDTO.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(null)
                .build();
        Mockito
                .when(itemService.addItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    Item b = (Item) (i.getArguments()[0]);
                    b = b.toBuilder().owner(u).build();
                    return ItemDTO.Controller.Mapper.toReturnItemDTO(b);
                });

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(item.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ownerId").value(user.getId()));
    }


    @Test
    public void addItemExceptions() throws Exception {
        itemDTO = ItemDTO.Controller.NewItemDTO.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(null)
                .requestId(null)
                .build();
        Mockito
                .when(itemService.addItem(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    Item b = (Item) (i.getArguments()[0]);
                    b = b.toBuilder().owner(u).build();
                    return ItemDTO.Controller.Mapper.toReturnItemDTO(b);
                });

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateItemTest() throws Exception {
        itemDTO = ItemDTO.Controller.NewItemDTO.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(null)
                .build();
        Mockito
                .when(itemService.updateItem(Mockito.any(Item.class), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    Item b = (Item) (i.getArguments()[0]);
                    b = b.toBuilder().owner(u).id((Long) (i.getArguments()[2])).build();
                    return ItemDTO.Controller.Mapper.toReturnItemDTO(b);
                });

        mvc.perform(patch("/items/{itemId}", item.getId())
                        .content(mapper.writeValueAsString(itemDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(item.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(item.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ownerId").value(user.getId()));
    }

    @Test
    public void getItemTest() throws Exception {
        Mockito
                .when(itemService.getItem(Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    Item b = Item.builder().id((Long) (i.getArguments()[0])).build();
                    b = b.toBuilder().owner(u).build();
                    return ItemDTO.Controller.Mapper.toReturnItemWithBookingsDTO(b, null, null, List.of());
                });

        mvc.perform(get("/items/{itemId}", item.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(item.getId()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.ownerId").value(user.getId()));
    }

    @Test
    public void searchItemTest() throws Exception {
        Mockito
                .when(itemService.searchItems(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt()))
                .thenAnswer(i -> {
                    User u = User.builder().id(45L).build();
                    Item b = Item.builder().owner(u).name((String) (i.getArguments()[0])).build();
                    return List.of(ItemDTO.Controller.Mapper.toReturnItemDTO(b));
                });

        mvc.perform(get("/items/search")
                        .param("text", "i hate tests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name").value("i hate tests"));
    }

    @Test
    public void commentsTest() throws Exception {
        CommentDTO.Controller.NewCommentDTO commentDTO = CommentDTO.Controller.NewCommentDTO.builder().text("werw").build();
        Mockito
                .when(itemService.addComment(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(i -> {
                    User u = User.builder().id((Long) (i.getArguments()[1])).build();
                    Item b = Item.builder().owner(u).id((Long) (i.getArguments()[2])).comments(new ArrayList<>()).build();
                    Comment com = (Comment) (i.getArguments()[0]);
                    b.addComment(com);
                    return CommentDTO.Controller.Mapper.toReturnCommentDTO(com);
                });

        mvc.perform(post("/items/{itemId}/comment", item.getId())
                        .content(mapper.writeValueAsString(commentDTO))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.text").value(commentDTO.getText()));
    }

    @Test
    public void returnItems() throws Exception {
        ItemDTO.Controller.ReturnItemWithBookingsDTO itemDTO
                = ItemDTO.Controller.ReturnItemWithBookingsDTO.returnItemWithBookingsDTOBuilder()
                .ownerId(user.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(null)
                .build();
        Mockito
                .when(itemService.getAllItems(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of(itemDTO));

        mvc.perform(get("/items")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").value(item.getDescription()))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].ownerId").value(user.getId()));
    }

    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String itemName = "Щетка";
    String itemDescription = "something";
    Boolean itemAvailable = true;
    Long userId = 2L;
    Long itemId = 25L;
    User user = null;
    Item item = null;


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
    }

}