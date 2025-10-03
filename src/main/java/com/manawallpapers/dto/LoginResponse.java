package com.manawallpapers.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Builder
@Getter
@Setter
public class LoginResponse {
    private String accessToken;
    private String tokenType;
    private UUID userId;
    private String email;
    private String fullName;
    private String role;
}