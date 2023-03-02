package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDTO;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDTO;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ItemDTOTest {

    @Test
    void values() {
        User user = User.builder().email("qwe@yandex.ru").name("joe").build();
        Item item = Item.builder().name("name").owner(user).build();
        UserDTO.Controller.ReturnUserDTO userDto = UserDTO.Controller.Mapper.toReturnUserDTO(user);
        ItemDTO.Controller.ReturnItemDTO itemDto = ItemDTO.Controller.Mapper.toReturnItemDTO(item);
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(itemDto.getName()).isEqualTo(item.getName());

        ItemDTO[] itemDTO = ItemDTO.values();
        assertThat(itemDTO.length).isEqualTo(0);
        ItemDTO.Controller[] itemDTOController = ItemDTO.Controller.values();
        assertThat(itemDTOController.length).isEqualTo(0);
        CommentDTO[] commentDTO = CommentDTO.values();
        assertThat(commentDTO.length).isEqualTo(0);
        CommentDTO.Controller[] commentDTOController = CommentDTO.Controller.values();
        assertThat(commentDTOController.length).isEqualTo(0);
        BookingDTO[] bookingDTO = BookingDTO.values();
        assertThat(bookingDTO.length).isEqualTo(0);
        BookingDTO.Controller[] bookingDTOC = BookingDTO.Controller.values();
        assertThat(bookingDTOC.length).isEqualTo(0);
        ItemRequestDTO[] itemRequestDTO = ItemRequestDTO.values();
        assertThat(itemRequestDTO.length).isEqualTo(0);
        ItemRequestDTO.Controller[] itemRequestDTOC = ItemRequestDTO.Controller.values();
        assertThat(itemRequestDTOC.length).isEqualTo(0);
        UserDTO[] userDTO = UserDTO.values();
        assertThat(userDTO.length).isEqualTo(0);
        UserDTO.Controller[] userDTOC = UserDTO.Controller.values();
        assertThat(userDTOC.length).isEqualTo(0);
    }
}