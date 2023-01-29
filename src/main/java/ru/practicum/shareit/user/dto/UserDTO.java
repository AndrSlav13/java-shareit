package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.Pattern;

public enum UserDTO {;

    private interface Email {
        @Pattern(regexp = "[a-zA-Z_-][a-zA-Z0-9_-]+@[a-zA-Z]+\\.[a-zA-Z]+")
        String getEmail();
    }

    public enum Controller {;

        @Data
        @Builder
        public static class NewUserDTO implements Email {
            private String name;
            private String email;
        }

        @Data
        @Builder
        public static class ReturnUserDTO implements Email {
            private Integer id;
            private String name;
            private String email;
        }

        public static class Mapper {
            public static ReturnUserDTO ToReturnUserDTO(User user) {
                ReturnUserDTO item = ReturnUserDTO.builder()
                        .name(user.getName())
                        .id(user.getId())
                        .email(user.getEmail())
                        .build();

                return item;
            }

            public static User ToUser(NewUserDTO userDTO) {
                User item = User.builder()
                        .name(userDTO.getName())
                        .id(null)
                        .email(userDTO.getEmail())
                        .build();

                return item;
            }

            public static User ToUser(NewUserDTO userDTO, Integer userId) {
                User item = User.builder()
                        .name(userDTO.getName())
                        .id(userId)
                        .email(userDTO.getEmail())
                        .build();

                return item;
            }
        }
    }

    public enum Database {;

        @Data
        @Builder
        public static class DBUserDTO implements Email {
            private Integer id;
            private String name;
            private String email;
        }

        public static class Mapper {
            public static DBUserDTO ToDBUserDTO(User user) {
                DBUserDTO item = DBUserDTO.builder()
                        .name(user.getName())
                        .id(user.getId())
                        .email(user.getEmail())
                        .build();

                return item;
            }

            public static User ToUser(DBUserDTO userDTO) {
                User item = User.builder()
                        .name(userDTO.name)
                        .id(userDTO.id)
                        .email(userDTO.email)
                        .build();

                return item;
            }
        }
    }
}
