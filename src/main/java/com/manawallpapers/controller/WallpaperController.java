package com.manawallpapers.controller;

import com.manawallpapers.dto.ApiResponse;
import com.manawallpapers.dto.WallpaperDto;
import com.manawallpapers.entity.User;
import com.manawallpapers.security.CustomUserDetails;
import com.manawallpapers.service.WallpaperService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/wallpapers")
public class WallpaperController {

    private WallpaperService wallpaperService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WallpaperDto>>> getAllWallpapers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean free) {

        Sort sort = Sort.by(sortDir.equalsIgnoreCase("desc") ?
                Sort.Direction.DESC : Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<WallpaperDto> wallpapers = wallpaperService.getAllWallpapers(pageable, category, q, free);
        return ResponseEntity.ok(ApiResponse.success(wallpapers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WallpaperDto>> getWallpaperById(@PathVariable UUID id) {
        WallpaperDto wallpaper = wallpaperService.getWallpaperById(id);
        return ResponseEntity.ok(ApiResponse.success(wallpaper));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<WallpaperDto>> createWallpaper(
            @RequestBody WallpaperDto wallpaperDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User uploader = new User();
        uploader.setId(userDetails.getId());

        WallpaperDto created = wallpaperService.createWallpaper(wallpaperDto, uploader);
        return ResponseEntity.ok(ApiResponse.success("Wallpaper created successfully", created));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteWallpaper(
            @PathVariable UUID id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        User user = new User();
        user.setId(userDetails.getId());
        user.setRole(String.valueOf(User.Role.ADMIN));

        wallpaperService.deleteWallpaper(id, user);
        return ResponseEntity.ok(ApiResponse.success("Wallpaper deleted successfully", null));
    }

    @PostMapping("/upload-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> generateUploadUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {

        String uploadUrl = wallpaperService.generateUploadUrl(filename);
        return ResponseEntity.ok(ApiResponse.success("Upload URL generated", uploadUrl));
    }
}