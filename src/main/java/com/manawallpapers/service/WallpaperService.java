package com.manawallpapers.service;

import com.manawallpapers.dto.WallpaperDto;
import com.manawallpapers.entity.User;
import com.manawallpapers.entity.Wallpaper;
import com.manawallpapers.exception.ResourceNotFoundException;
import com.manawallpapers.repository.WallpaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WallpaperService {
    @Autowired
    private WallpaperRepository wallpaperRepository;
    @Autowired
    private StorageService storageService;

    public List<WallpaperDto> getAllWallpapers(Pageable pageable, String category, Boolean free) {
        List<Wallpaper> wallpapers;
        wallpapers = wallpaperRepository.findAllByOrderByTitleAsc();
        if (category == null) {
            wallpapers = wallpaperRepository.findAllByOrderByTitleAsc();
        }
//        else if (category != null) {
//            wallpapers = wallpaperRepository.findByCategorySlug(category, pageable);
//        } else if (query != null) {
//            wallpapers = wallpaperRepository.findByTitleContainingIgnoreCase(query, pageable);
//        } else if (free != null) {
//            wallpapers = wallpaperRepository.findByIsFree(free, pageable);
//        } else {
//            wallpapers = wallpaperRepository.findByIsDownloadableTrue(pageable);
//        }


        return null;
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

//    public void deleteWallpaper(UUID id, User user) {
//        Wallpaper wallpaper = wallpaperRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Wallpaper not found with id: " + id));
//
//        // Check if user is admin or the uploader
//        if (!user.getRole().equals(User.Role.ADMIN) && !wallpaper.getUploader().getId().equals(user.getId())) {
//            throw new SecurityException("Not authorized to delete this wallpaper");
//        }
//
//        // Delete from Storage
//        storageService.deleteObject(wallpaper.getFileKey());
//
//
//        wallpaperRepository.delete(wallpaper);
//    }

//    public String generateUploadUrl(String filename) {
//        String key = "wallpapers/" + UUID.randomUUID() + "/" + filename;
//        return storageService.generatePresignedUploadUrl(key);
//    }

    private WallpaperDto convertToDto(Wallpaper wallpaper) {
        WallpaperDto dto = new WallpaperDto();
        dto.setId(wallpaper.getId());
        dto.setTitle(wallpaper.getTitle());
        dto.setDescription(wallpaper.getDescription());
        dto.setFileKey(wallpaper.getFileKey());
        dto.setPriceCents(wallpaper.getPriceCents());
        dto.setCurrency(wallpaper.getCurrency());
        dto.setIsFree(wallpaper.getIsFree());
        dto.setResolution(wallpaper.getResolution());
        dto.setFormat(wallpaper.getFormat());
        dto.setLicenseText(wallpaper.getLicenseText());
        dto.setTags(wallpaper.getTags());
        dto.setCreatedAt(wallpaper.getCreatedAt());
        dto.setUpdatedAt(wallpaper.getUpdatedAt());

//        if (wallpaper.getThumbnailKey() != null) {
//            dto.setThumbnailUrl(storageService.generatePresignedDownloadUrl(wallpaper.getThumbnailKey()));
//        }

        return dto;
    }
}