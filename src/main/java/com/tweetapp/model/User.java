package com.tweetapp.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tweetapp.domain.UserRegisterRequest;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Document
@ToString
public class User {
    @Id
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String loginId;
    //	@JsonIgnore
    private String password;
    private long phoneNumber;

    public static User buildUser(UserRegisterRequest userRegisterRequest) {
        return User.builder()
                .firstName(userRegisterRequest.getFirstName())
                .lastName(userRegisterRequest.getLastName())
                .email(userRegisterRequest.getEmail())
                .loginId(userRegisterRequest.getLoginId())
                .password(userRegisterRequest.getPassword())
                .phoneNumber(userRegisterRequest.getContactNo()).build();
    }
}
