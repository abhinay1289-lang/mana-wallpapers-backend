package com.manawallpapers.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Getter
@Setter
public class WallpaperResponse {
    private UUID id;
    private String title;
    private Integer priceCents;
    private Boolean isFree;
    private String resolution;
    private String format;
    private String fileKey;
    private LookupDto category;
    private LookupDto subCategory;
    private LookupDto miniSubCategory;
    private String tags;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;
}
