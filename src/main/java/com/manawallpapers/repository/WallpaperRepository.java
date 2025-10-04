package com.manawallpapers.repository;

import com.manawallpapers.entity.Wallpaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface WallpaperRepository extends JpaRepository<Wallpaper, UUID> {

    List<Wallpaper> findAllByOrderByTitleAsc();

    Page<Wallpaper> findByIsDownloadableTrue(Pageable pageable);

    Page<Wallpaper> findByIsFree(Boolean isFree, Pageable pageable);

    Page<Wallpaper> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}