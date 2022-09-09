package com.tweetapp.domain;

import com.tweetapp.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponse {
    private String loginId;
    private boolean valid;
    private String token;
}
