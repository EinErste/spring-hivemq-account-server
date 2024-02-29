package org.ein.erste.iot.account.domain.dto;

import lombok.Builder;
import lombok.Data;
import org.ein.erste.iot.account.domain.User;

import java.util.UUID;

@Data
@Builder
public class UserDTO {

    private UUID id;

    private String email;

    private String login;

    private String password;

    private String name;


    public User to(){
        return User.builder()
                .id(id)
                .email(email)
                .name(name)
                .build();
    }

    public static UserDTO of(User user){
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();
    }
}
