package com.manawallpapers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;
}