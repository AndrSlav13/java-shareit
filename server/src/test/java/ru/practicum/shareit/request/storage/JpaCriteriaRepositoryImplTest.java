package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class JpaCriteriaRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    void findRequestsTest() {
        User user = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail).name(userName).build();
        User user2 = User.builder().items(new ArrayList<>()).bookings(new ArrayList<>()).requests(new ArrayList<>())
                .email(userEmail2).name(userName2).build();
        ItemRequest request = ItemRequest.builder().description("req").requestor(user).build();
        ItemRequest request2 = ItemRequest.builder().description("req2").requestor(user).build();

        user = userRepository.save(user);
        user2 = userRepository.save(user2);
        request = requestRepository.save(request);
        request2 = requestRepository.save(request2);

        List<ItemRequest> b = requestRepository.findAllByRequestorId(user.getId());
        assertThat(b).isNotEmpty().hasSize(2);
        b = requestRepository.findAllByNotRequestorId(user2.getId(), 0, 10);
        assertThat(b).isNotEmpty().hasSize(2);
        b = requestRepository.findAllByNotRequestorId(user.getId(), 0, 10);
        assertThat(b).isEmpty();
        b = requestRepository.findAllByRequestorId(user2.getId());
        assertThat(b).isEmpty();
        b = requestRepository.findAllByIdNotNull();
        assertThat(b).isNotEmpty().hasSize(2);
    }


    String userEmail = "qwerty@yandex.ru";
    String userName = "John";
    String userEmail2 = "qwerty@google.ru";
    String userName2 = "Sam";

}