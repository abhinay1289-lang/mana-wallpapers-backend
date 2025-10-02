package com.manawallpapers.service;

import io.github.jan.supabase.SupabaseClient;
import io.github.jan.supabase.storage.Storage;
import io.github.jan.supabase.storage.StorageItem;
import kotlinx.coroutines.future.FutureKt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static io.github.jan.supabase.client.SupabaseClientKt.createSupabaseClient;
import static kotlin.time.Duration.Companion;

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
        supabaseClient = createSupabaseClient(supabaseUrl, supabaseKey, builder -> {
            builder.install(Storage.class);
            return null;
        });
    }

    public String generatePresignedUploadUrl(String key) {
        try {
            return FutureKt.asCompletableFuture(supabaseClient.getStorage().from(bucketName)
                    .createSignedUploadUrl(key, Companion.seconds(presignedUrlExpiration), false)).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error generating presigned upload URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }

    public String generatePresignedDownloadUrl(String key) {
        try {
            return FutureKt.asCompletableFuture(supabaseClient.getStorage().from(bucketName)
                    .createSignedUrl(key, Companion.seconds(presignedUrlExpiration))).get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error generating presigned download URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    public void deleteObject(String key) {
        try {
            FutureKt.asCompletableFuture(supabaseClient.getStorage().from(bucketName).delete(Collections.singletonList(key))).get();
            log.info("Successfully deleted object: {}", key);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error deleting object: {}", key, e);
            throw new RuntimeException("Failed to delete object", e);
        }
    }

    public boolean objectExists(String key) {
        try {
            List<StorageItem> files = FutureKt.asCompletableFuture(supabaseClient.getStorage().from(bucketName).list(key, 1, 0, null)).get();
            return files != null && !files.isEmpty();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Error checking if object exists: {}", key, e);
            return false;
        }
    }
}
