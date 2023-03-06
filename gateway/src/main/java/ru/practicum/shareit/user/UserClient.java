package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDTO;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.Map;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getAllUsers(Long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }


    public ResponseEntity<Object> addUser(UserDTO.Controller.NewUserDTO userDTO) {
        return post("", userDTO);
    }

    public ResponseEntity<Object> getUser(long userId, Long id) {
        return get("/" + id, userId);
    }

    public ResponseEntity<Object> updateUser(UserDTO.Controller.UpdateUserDTO userDTO, Long userId) {
        return patch("/" + userId, userDTO);
    }

    public ResponseEntity<Object> deleteUser(long userId, long id) {
        return delete("/" + id, userId);
    }

}
