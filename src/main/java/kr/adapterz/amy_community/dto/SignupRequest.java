package kr.adapterz.amy_community.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupRequest {

    @JsonProperty("email")
    @NotBlank(message = "email_required")
    @Email(message = "invalid_email_format")
    private String email;

    @JsonProperty("password")
    @NotBlank(message = "password_required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=<>?]).{8,20}$",
            message = "invalid_password_format"
    )
    private String password;

    @JsonProperty("passwordCheck")
    @NotBlank(message = "password_check_required")
    private String passwordCheck;

    @JsonProperty("nickname")
    @NotBlank(message = "nickname_required")
    @Size(max = 10, message = "nickname_length")
    private String nickname;

    @JsonProperty("profileImageBase64")
    private String profileImageBase64;
}