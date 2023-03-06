package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

public enum UserDTO {
    ;

    public enum Controller {
        ;

        @Builder
        @Getter
        @Setter
        public static class UpdateUserDTO {
            private String name;
            private String email;

            public UpdateUserDTO(String name, String email) {
                this.email = email;
                this.name = name;
            }
        }

        @Getter
        @Setter
        public static class NewUserDTO extends UpdateUserDTO {
            @Builder(builderMethodName = "newUserDTOBuilder")
            public NewUserDTO(String name, String email) {
                super(name, email);
            }
        }

        @Data
        @Builder
        public static class ReturnUserDTO {
            private Long id;
            private String name;
            private String email;
        }

        public static class Mapper {
            public static ReturnUserDTO toReturnUserDTO(User user) {
                ReturnUserDTO item = ReturnUserDTO.builder()
                        .name(user.getName())
                        .id(user.getId())
                        .email(user.getEmail())
                        .build();

                return item;
            }

            public static User toUser(UpdateUserDTO userDTO) {
                User item = User.builder()
                        .name(userDTO.getName())
                        .id(null)
                        .email(userDTO.getEmail())
                        .items(new ArrayList<>())
                        .bookings(new ArrayList<>())
                        .requests(new ArrayList<>())
                        .build();

                return item;
            }

        }
    }

}
