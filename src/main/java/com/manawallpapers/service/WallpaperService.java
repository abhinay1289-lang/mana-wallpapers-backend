package com.manawallpapers.service;

import com.manawallpapers.dto.WallpaperDto;
import com.manawallpapers.entity.User;
import com.manawallpapers.entity.Wallpaper;
import com.manawallpapers.exception.ResourceNotFoundException;
import com.manawallpapers.repository.WallpaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class WallpaperService {

    private final WallpaperRepository wallpaperRepository;
    private final StorageService storageService;

    public Page<WallpaperDto> getAllWallpapers(Pageable pageable, String category, String query, Boolean free) {
        Page<Wallpaper> wallpapers;

        if (category != null && query != null) {
            wallpapers = wallpaperRepository.findByCategorySlugAndTitleContainingIgnoreCase(category, query, pageable);
        } else if (category != null) {
            wallpapers = wallpaperRepository.findByCategorySlug(category, pageable);
        } else if (query != null) {
            wallpapers = wallpaperRepository.findByTitleContainingIgnoreCase(query, pageable);
        } else if (free != null) {
            wallpapers = wallpaperRepository.findByIsFree(free, pageable);
        } else {
            wallpapers = wallpaperRepository.findByIsDownloadableTrue(pageable);
        }

        return wallpapers.map(this::convertToDto);
    }

    public WallpaperDto getWallpaperById(UUID id) {
        Wallpaper wallpaper = wallpaperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallpaper not found with id: " + id));

        return convertToDto(wallpaper);
    }

    public WallpaperDto createWallpaper(WallpaperDto dto, User uploader) {
        Wallpaper wallpaper = new Wallpaper();
        wallpaper.setTitle(dto.getTitle());
        wallpaper.setDescription(dto.getDescription());
        wallpaper.setFileKey(dto.getFileKey());
        wallpaper.setThumbnailKey(dto.getThumbnailKey());
        wallpaper.setPriceCents(dto.getPriceCents());
        wallpaper.setCurrency(dto.getCurrency());
        wallpaper.setIsFree(dto.getIsFree());
        wallpaper.setIsDownloadable(true);
        wallpaper.setResolution(dto.getResolution());
        wallpaper.setFormat(dto.getFormat());
        wallpaper.setLicenseText(dto.getLicenseText());
        wallpaper.setUploader(uploader);
        wallpaper.setTags(dto.getTags());

        wallpaper = wallpaperRepository.save(wallpaper);
        return convertToDto(wallpaper);
    }

    public void deleteWallpaper(UUID id, User user) {
        Wallpaper wallpaper = wallpaperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallpaper not found with id: " + id));

        // Check if user is admin or the uploader
        if (!user.getRole().equals(User.Role.ADMIN) && !wallpaper.getUploader().getId().equals(user.getId())) {
            throw new SecurityException("Not authorized to delete this wallpaper");
        }

        // Delete from Storage
        storageService.deleteObject(wallpaper.getFileKey());
        if (wallpaper.getThumbnailKey() != null) {
            storageService.deleteObject(wallpaper.getThumbnailKey());
        }

        wallpaperRepository.delete(wallpaper);
    }

    public String generateUploadUrl(String filename) {
        String key = "wallpapers/" + UUID.randomUUID() + "/" + filename;
        return storageService.generatePresignedUploadUrl(key);
    }

    private WallpaperDto convertToDto(Wallpaper wallpaper) {
        WallpaperDto dto = new WallpaperDto();
        dto.setId(wallpaper.getId());
        dto.setTitle(wallpaper.getTitle());
        dto.setDescription(wallpaper.getDescription());
        dto.setFileKey(wallpaper.getFileKey());
        dto.setThumbnailKey(wallpaper.getThumbnailKey());
        dto.setPriceCents(wallpaper.getPriceCents());
        dto.setCurrency(wallpaper.getCurrency());
        dto.setIsFree(wallpaper.getIsFree());
        dto.setResolution(wallpaper.getResolution());
        dto.setFormat(wallpaper.getFormat());
        dto.setLicenseText(wallpaper.getLicenseText());
        dto.setTags(wallpaper.getTags());
        dto.setCreatedAt(wallpaper.getCreatedAt());
        dto.setUpdatedAt(wallpaper.getUpdatedAt());

        if (wallpaper.getThumbnailKey() != null) {
            dto.setThumbnailUrl(storageService.generatePresignedDownloadUrl(wallpaper.getThumbnailKey()));
        }

        return dto;
    }
}