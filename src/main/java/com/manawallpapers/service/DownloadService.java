package com.manawallpapers.service;

import com.manawallpapers.entity.Download;
import com.manawallpapers.entity.Order;
import com.manawallpapers.entity.OrderItem;
import com.manawallpapers.entity.User;
import com.manawallpapers.exception.ResourceNotFoundException;
import com.manawallpapers.repository.DownloadRepository;
import com.manawallpapers.repository.WallpaperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class DownloadService {
    @Autowired
    private DownloadRepository downloadRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private WallpaperRepository wallpaperRepository;

    public void createDownloadTokens(Order order) {
        for (OrderItem item : order.getOrderItems()) {
            Download download = new Download();
            download.setWallpaper(item.getWallpaper());
            download.setBuyer(order.getBuyer());
            download.setOrder(order);
            download.setToken(UUID.randomUUID().toString());
            download.setExpiresAt(LocalDateTime.now().plusDays(30)); // 30 days to download
            download.setDownloadCount(0);

            downloadRepository.save(download);
            log.info("Created download token for wallpaper: {} for user: {}",
                    item.getWallpaper().getId(), order.getBuyer().getId());
        }
    }

    public String generateDownloadUrl(String token, String ipAddress, User user) {
        Download download = downloadRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Download token not found"));

        // Validate token
        if (download.isExpired()) {
            throw new IllegalStateException("Download token has expired");
        }

        if (!download.getBuyer().getId().equals(user.getId())) {
            throw new SecurityException("Not authorized to download this file");
        }

        // Update download record
        download.setIpAddress(ipAddress);
        download.setDownloadCount(download.getDownloadCount() + 1);
        downloadRepository.save(download);

        // Generate presigned URL
        return storageService.generatePresignedDownloadUrl(download.getWallpaper().getFileKey());
    }

    public Download createFreeDownload(UUID wallpaperId, User user, String ipAddress) {
        // For free wallpapers, create temporary download
        Download download = new Download();
        download.setWallpaper(wallpaperRepository.findById(wallpaperId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallpaper not found")));
        download.setBuyer(user);
        download.setToken(UUID.randomUUID().toString());
        download.setExpiresAt(LocalDateTime.now().plusHours(1)); // 1 hour for free downloads
        download.setIpAddress(ipAddress);
        download.setDownloadCount(0);

        return downloadRepository.save(download);
    }
}