package com.manawallpapers.repository;

import com.manawallpapers.entity.MiniSubCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;
@Repository
public interface MiniSubCategoryRepository extends JpaRepository<MiniSubCategory, UUID> {
}