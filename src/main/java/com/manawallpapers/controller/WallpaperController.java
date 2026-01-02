package com.manawallpapers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.manawallpapers.dto.*;
import com.manawallpapers.entity.User;
import com.manawallpapers.security.CustomUserDetails;
import com.manawallpapers.service.WallpaperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/wallpapers")
public class WallpaperController {
    @Autowired
    private WallpaperService wallpaperService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<ApiResponse<List<WallpaperResponse>>> getAllWallpapers(
            @RequestParam(required = false) UUID typeId
            ) {
        List<WallpaperResponse> wallpapers = wallpaperService.getAllWallpapers(typeId);
        return ResponseEntity.ok(ApiResponse.success(wallpapers));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WallpaperDto>> getWallpaperById(@PathVariable UUID id) {
        WallpaperDto wallpaper = wallpaperService.getWallpaperById(id);
        return ResponseEntity.ok(ApiResponse.success(wallpaper));
    }

    @GetMapping("/all-category")
    public ResponseEntity<ApiResponse<List<CategoryDto>>> getAllCategoriesAndSubCategories() {
        List<CategoryDto> wallpaper = wallpaperService.getAllCategoriesAndSubCategories();
        return ResponseEntity.ok(ApiResponse.success(wallpaper));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<WallpaperDto>> createWallpaper(
            @RequestPart("wallpaperDto")
            String wallpaperDtoJson,
            @RequestPart("file") MultipartFile file)  {
        WallpaperDto wallpaperDto;
        try {
             wallpaperDto = objectMapper.readValue(wallpaperDtoJson, WallpaperDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON for WallpaperDto", e);
        }
        WallpaperDto created = wallpaperService.createWallpaper(wallpaperDto,file);
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

//        wallpaperService.deleteWallpaper(id, user);
        return ResponseEntity.ok(ApiResponse.success("Wallpaper deleted successfully", null));
    }

    @PostMapping("/upload-url")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> generateUploadUrl(
            @RequestParam String filename,
            @RequestParam String contentType) {

//        String uploadUrl = wallpaperService.generateUploadUrl(filename);
        return ResponseEntity.ok(ApiResponse.success("Upload URL generated", ""));
    }

    @PatchMapping("/saved")
    public ResponseEntity<ApiResponse<String>> saveWallpaper(@RequestBody SaveOrLikeWallpaperRequest saveOrLikeWallpaperRequest){
        wallpaperService.saveWallpaper(saveOrLikeWallpaperRequest.getId(),saveOrLikeWallpaperRequest.getIsLikedOrSaved());
        return ResponseEntity.ok(ApiResponse.success("Wallpaper saved successfully", ""));
    }

    @PatchMapping("/liked")
    public ResponseEntity<ApiResponse<String>> likeWallpaper(@RequestBody SaveOrLikeWallpaperRequest saveOrLikeWallpaperRequest){
        wallpaperService.likeWallpaper(saveOrLikeWallpaperRequest.getId(), saveOrLikeWallpaperRequest.getIsLikedOrSaved());
        return ResponseEntity.ok(ApiResponse.success("Wallpaper liked successfully", ""));
    }
}