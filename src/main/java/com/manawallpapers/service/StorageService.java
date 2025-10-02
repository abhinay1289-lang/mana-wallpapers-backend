package com.manawallpapers.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Collections;

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

    private final RestTemplate restTemplate = new RestTemplate();

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(supabaseKey);
        headers.set("apikey", supabaseKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    /**
     * Generate a presigned upload URL for a given file key
     */
    public String generatePresignedUploadUrl(String key) {
        try {
            String url = supabaseUrl + "/storage/v1/object/sign/" + bucketName + "/" + key;
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("expiresIn", presignedUrlExpiration)
                    .queryParam("upsert", false);

            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error generating presigned upload URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate upload URL", e);
        }
    }

    /**
     * Generate a presigned download URL for a given file key
     */
    public String generatePresignedDownloadUrl(String key) {
        try {
            String url = supabaseUrl + "/storage/v1/object/sign/" + bucketName + "/" + key;
            UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(url)
                    .queryParam("expiresIn", presignedUrlExpiration);

            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());
            ResponseEntity<String> response = restTemplate.exchange(
                    builder.toUriString(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Error generating presigned download URL for key: {}", key, e);
            throw new RuntimeException("Failed to generate download URL", e);
        }
    }

    /**
     * Delete an object from storage
     */
    public void deleteObject(String key) {
        try {
            String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + key;
            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());

            restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    entity,
                    String.class
            );

            log.info("Successfully deleted object: {}", key);
        } catch (Exception e) {
            log.error("Error deleting object: {}", key, e);
            throw new RuntimeException("Failed to delete object", e);
        }
    }

    /**
     * Check if object exists
     */
    public boolean objectExists(String key) {
        try {
            String url = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + key;
            HttpEntity<String> entity = new HttpEntity<>(buildHeaders());

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.HEAD,
                    entity,
                    String.class
            );

            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.warn("Object {} does not exist or error occurred", key, e);
            return false;
        }
    }
}
