package com.manawallpapers.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wallpapers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Wallpaper {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank
    @Column(name = "title", nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotBlank
    @Column(name = "file_key", nullable = false)
    private String fileKey;

    @Column(name = "price_cents")
    private Integer priceCents;

    @Column(nullable = false, length = 3)
    private String currency = "USD";

    @Column(name = "is_free", nullable = false)
    private Boolean isFree = false;

    @Column(name = "is_downloadable", nullable = false)
    private Boolean isDownloadable = true;

    @Column(nullable = false)
    private String resolution;

    @Column(nullable = false)
    private String format;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sub_category_id", nullable = false)
    private SubCategory subCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mini_sub_category_id", nullable = false)
    private MiniSubCategory miniSubCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;

    @Column(name = "tags")
    private String tags;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public BigDecimal getPrice() {
        if (priceCents == null) return BigDecimal.ZERO;
        return BigDecimal.valueOf(priceCents).divide(BigDecimal.valueOf(100));
    }

    public void setPrice(BigDecimal price) {
        if (price == null) {
            this.priceCents = null;
        } else {
            this.priceCents = price.multiply(BigDecimal.valueOf(100)).intValue();
        }
    }
}