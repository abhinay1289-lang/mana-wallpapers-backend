package com.manawallpapers.dto;

import com.manawallpapers.entity.User;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private UUID userId;
    private String email;
    private String fullName;
    private User.Role role;
}