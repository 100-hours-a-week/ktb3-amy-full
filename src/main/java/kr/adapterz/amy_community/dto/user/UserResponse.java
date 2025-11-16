package kr.adapterz.amy_community.dto.user;

import kr.adapterz.amy_community.entity.UserEntity;
import lombok.Getter;

@Getter
public class UserResponse {

    private Long id;
    private String email;
    private String nickname;
    private String profileImage;

    public UserResponse(UserEntity user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();

        if (user.getProfileImage() != null) {
            this.profileImage = "data:image/png;base64," + user.getProfileImage();
        }
    }
}
