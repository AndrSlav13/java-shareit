package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;

public enum UserDTO {
    ;

    private interface Email {
        @Pattern(regexp = "[a-zA-Z_-][a-zA-Z0-9_-]+@[a-zA-Z]+\\.[a-zA-Z]+")
        @NotNull
        String getEmail();
    }

    private interface Name {
        @NotNull
        @NotBlank
        String getName();
    }

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
        public static class NewUserDTO extends UpdateUserDTO implements Email, Name {
            @Builder(builderMethodName = "newUserDTOBuilder")
            public NewUserDTO(String name, String email) {
                super(name, email);
            }
        }

        @Data
        @Builder
        public static class ReturnUserDTO implements Email {
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
