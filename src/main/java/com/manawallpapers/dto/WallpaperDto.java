package com.manawallpapers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Getter
@Setter
public class WallpaperDto {
    private UUID id;
    private String title;
    private String description;
    private String fileKey;
    private String thumbnailKey;
    private String thumbnailUrl;
    private Integer priceCents;
    private String currency;
    private Boolean isFree;
    private String resolution;
    private String format;
    private String licenseText;
    private List<String> tags;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}