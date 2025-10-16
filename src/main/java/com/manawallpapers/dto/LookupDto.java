package com.manawallpapers.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
@Builder
public class LookupDto {
    private UUID id;
    private String name;
}
