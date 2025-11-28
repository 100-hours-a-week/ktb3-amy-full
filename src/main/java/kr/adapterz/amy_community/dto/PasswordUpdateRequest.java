package kr.adapterz.amy_community.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PasswordUpdateRequest {

    @NotBlank(message = "password_required")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()_\\-+=<>?]).{8,20}$",
            message = "invalid_password_format"
    )
    private String newPassword;

    @NotBlank(message = "password_check_required")
    private String newPasswordCheck;
}