package kr.adapterz.amy_community.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserSignupRequest {
    private String email;
    private String password;
    private String nickname;
    private String profileImage;
}