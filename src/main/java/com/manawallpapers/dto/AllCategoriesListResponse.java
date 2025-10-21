package com.manawallpapers.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AllCategoriesListResponse {
private UUID id;
private String name;
private List<WallpaperDto> wallpapers;
}
