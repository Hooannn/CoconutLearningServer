package com.ht.elearning.auth;

import com.ht.elearning.user.User;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {
    private Credentials credentials;
    private User user;
}

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
class Credentials {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;
}