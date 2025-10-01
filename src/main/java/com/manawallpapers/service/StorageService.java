package com.manawallpapers.service;

import io.supabase.client.SupabaseClient;
import io.supabase.client.storage.StorageFile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.URL;
import java.util.List;

@Service
@Slf4j
public class StorageService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.storage.bucket-name}")
    private String bucketName;

    @Value("${app.presigned-url.expiration:3600}")
    private long presignedUrlExpiration;

    private SupabaseClient supabaseClient;

    @PostConstruct
    public void initialize() {
        this.supabaseClient = new SupabaseClient(supabaseUrl, supabaseKey, null);
    }

    public String generatePresignedUploadUrl(String key, String contentType) {
        try {
            return supabaseClient.getStorage().from(bucketName).createSignedUrl(key, presignedUrlExpiration);
        } catch (Exception e) {
            log.error("Error generating presigned upload URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }

    public String generatePresignedDownloadUrl(String key) {
        try {
            return supabaseClient.getStorage().from(bucketName).createSignedUrl(key, presignedUrlExpiration);
        } catch (Exception e) {
            log.error("Error generating presigned download URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    public void deleteObject(String key) {
        try {
            supabaseClient.getStorage().from(bucketName).remove(List.of(key));
            log.info("Successfully deleted object: {}", key);
        } catch (Exception e) {
            log.error("Error deleting object: {}", key, e);
            throw new RuntimeException("Failed to delete object", e);
        }
    }

    public boolean objectExists(String key) {
        try {
            List<StorageFile> files = supabaseClient.getStorage().from(bucketName).list(key, null);
            return files != null && !files.isEmpty();
        } catch (Exception e) {
            log.error("Error checking if object exists: {}", key, e);
            return false;
        }
    }
}
