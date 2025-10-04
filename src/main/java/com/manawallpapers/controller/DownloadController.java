package com.manawallpapers.controller;

import com.manawallpapers.dto.ApiResponse;
import com.manawallpapers.entity.User;
import com.manawallpapers.security.CustomUserDetails;
import com.manawallpapers.service.DownloadService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/download")
public class DownloadController {

    private DownloadService downloadService;

    @GetMapping("/{token}")
    public ResponseEntity<ApiResponse<String>> generateDownloadUrl(
            @PathVariable String token,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) {

        User user = new User();
        user.setId(userDetails.getId());

        String ipAddress = getClientIpAddress(request);
//        String downloadUrl = downloadService.generateDownloadUrl(token, ipAddress, user);

        return ResponseEntity.ok(ApiResponse.success("Download URL generated", ""));
    }

    @PostMapping("/free/{wallpaperId}")
    public ResponseEntity<ApiResponse<String>> createFreeDownload(
            @PathVariable UUID wallpaperId,
            @AuthenticationPrincipal CustomUserDetails userDetails,
            HttpServletRequest request) {

        User user = new User();
        user.setId(userDetails.getId());

        String ipAddress = getClientIpAddress(request);
        var download = downloadService.createFreeDownload(wallpaperId, user, ipAddress);

        // Generate immediate download URL for free wallpapers
//        String downloadUrl = downloadService.generateDownloadUrl(download.getToken(), ipAddress, user);

        return ResponseEntity.ok(ApiResponse.success("Free download URL generated", ""));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}