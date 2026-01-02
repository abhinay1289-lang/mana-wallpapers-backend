package com.manawallpapers.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SaveOrLikeWallpaperRequest {
    private UUID id;
    private Boolean isLikedOrSaved;
}
