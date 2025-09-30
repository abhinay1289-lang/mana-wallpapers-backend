package com.manawallpapers.repository;

import com.manawallpapers.entity.Wallpaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WallpaperRepository extends JpaRepository<Wallpaper, UUID> {

    Page<Wallpaper> findByIsDownloadableTrue(Pageable pageable);

    Page<Wallpaper> findByIsFree(Boolean isFree, Pageable pageable);

    Page<Wallpaper> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    @Query("SELECT w FROM Wallpaper w WHERE w.category.slug = :categorySlug AND w.isDownloadable = true")
    Page<Wallpaper> findByCategorySlug(@Param("categorySlug") String categorySlug, Pageable pageable);

    @Query("SELECT w FROM Wallpaper w WHERE w.category.slug = :categorySlug AND UPPER(w.title) LIKE UPPER(CONCAT('%', :title, '%')) AND w.isDownloadable = true")
    Page<Wallpaper> findByCategorySlugAndTitleContainingIgnoreCase(
            @Param("categorySlug") String categorySlug,
            @Param("title") String title,
            Pageable pageable);
}