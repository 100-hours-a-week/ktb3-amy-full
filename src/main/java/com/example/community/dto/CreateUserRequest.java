package com.example.community.dto;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String email;
    private String password;
    private String nickname;
    private String profile_image;
}
