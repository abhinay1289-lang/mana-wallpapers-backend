package com.manawallpapers.service;

import com.manawallpapers.dto.*;
import com.manawallpapers.entity.*;
import com.manawallpapers.exception.ResourceNotFoundException;
import com.manawallpapers.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WallpaperService {
    @Autowired
    private WallpaperRepository wallpaperRepository;
    @Autowired
    private StorageService storageService;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubCategoryRepository subCategoryRepository;
    @Autowired
    private MiniSubCategoryRepository miniSubCategoryRepository;
    @Autowired
    private UserRepository userRepository;

    public List<WallpaperDto> getAllWallpapers(Pageable pageable, String category, Boolean free) {
        List<Wallpaper> wallpapers;
        wallpapers = wallpaperRepository.findAllByOrderByTitleAsc();
        if (category == null) {
            wallpapers = wallpaperRepository.findAllByOrderByTitleAsc();
        }
//        else if (category != null) {
//            wallpapers = wallpaperRepository.findByCategorySlug(category, pageable);
//        } else if (query != null) {
//            wallpapers = wallpaperRepository.findByTitleContainingIgnoreCase(query, pageable);
//        } else if (free != null) {
//            wallpapers = wallpaperRepository.findByIsFree(free, pageable);
//        } else {
//            wallpapers = wallpaperRepository.findByIsDownloadableTrue(pageable);
//        }


        return null;
    }

    public WallpaperDto getWallpaperById(UUID id) {
        Wallpaper wallpaper = wallpaperRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Wallpaper not found with id: " + id));

        return convertToDto(wallpaper);
    }

    public List<AllCategoriesListResponse> getAllCategoriesAndSubCategories() {
        List<MiniSubCategory> categories = miniSubCategoryRepository.findAll();
        return mapToCategoryDto(categories);
    }

    public List<AllCategoriesListResponse> mapToCategoryDto(List<MiniSubCategory> miniCategoryBos) {

        // Group MiniCategoryBos by SubCategoryBo
        Map<SubCategory, List<MiniSubCategory>> miniBySubCategory = miniCategoryBos.stream()
                .collect(Collectors.groupingBy(MiniSubCategory::getSubCategory));

        // Map each SubCategoryBo to SubCategoryDto with its Minis as items
        Map<Category, List<SubCategoryDto>> subCategoriesByCategory = new HashMap<>();

        for (Map.Entry<SubCategory, List<MiniSubCategory>> entry : miniBySubCategory.entrySet()) {
            SubCategory subCategoryBo = entry.getKey();
            List<MiniSubCategory> miniList = entry.getValue();

            // Convert MiniCategoryBos to ItemDtos
            List<MiniSubCategoryDto> itemDtos = miniList.stream()
                    .map(mini -> new MiniSubCategoryDto(mini.getId(), mini.getName()))
                    .collect(Collectors.toList());

            SubCategoryDto subCategoryDto = new SubCategoryDto(
                    subCategoryBo.getId(),
                    subCategoryBo.getName(),
                    itemDtos
            );
            Category categoryBo = subCategoryBo.getCategory();

            // Group subcategories by their parent CategoryBo
            subCategoriesByCategory.computeIfAbsent(categoryBo, k -> new ArrayList<>())
                    .add(subCategoryDto);
        }

        // Map each CategoryBo to CategoryDto with its SubCategories
        return subCategoriesByCategory.entrySet().stream()
                .map(entry -> {
                    Category categoryBo = entry.getKey();
                    List<SubCategoryDto> subCategoryDtos = entry.getValue();

                    return new AllCategoriesListResponse(
                            categoryBo.getId(),
                            categoryBo.getName(),
                            subCategoryDtos
                    );
                })
                .collect(Collectors.toList());
    }

    public List<MiniSubCategoryDto> getAllMiniSubCategories() {
        List<MiniSubCategoryDto> subCategoryDtos = new ArrayList<>();
        List<MiniSubCategory> categories = miniSubCategoryRepository.findAll();
        categories.forEach(category -> {
            MiniSubCategoryDto categoryDto = new MiniSubCategoryDto();
            categoryDto.setId(category.getId());
            categoryDto.setName(category.getName());
            subCategoryDtos.add(categoryDto);
        });
        return subCategoryDtos;
    }

    public WallpaperDto createWallpaper(WallpaperDto dto, MultipartFile imageFile) {
        Wallpaper wallpaper = new Wallpaper();
        Category category = categoryRepository.findById(dto.getCategory()).get();
        SubCategory subCategory = subCategoryRepository.findById(dto.getSubCategory()).get();
        MiniSubCategory miniSubCategory = miniSubCategoryRepository.findById(dto.getMiniSubCategory()).get();
        Optional<User> user  = userRepository.findById(dto.getUploadedBy());
        String path = category.getName().replace(" ", "-") +"/"+ subCategory.getName() +"/"+ miniSubCategory.getName();
        String fileKey = storageService.uploadFile(imageFile,path);
        wallpaper.setTitle(dto.getTitle());
        wallpaper.setDescription(dto.getDescription());
        wallpaper.setFileKey(fileKey);
        wallpaper.setPriceCents(dto.getPriceCents());
        wallpaper.setCurrency(dto.getCurrency());
        wallpaper.setIsFree(dto.getIsFree());
        wallpaper.setIsDownloadable(true);
        wallpaper.setResolution(dto.getResolution());
        wallpaper.setFormat(dto.getFormat());
        wallpaper.setCategory(category);
        wallpaper.setSubCategory(subCategory);
        wallpaper.setMiniSubCategory(miniSubCategory);
        if(user.isPresent()){
            wallpaper.setUploader(user.get());
        }
        wallpaper.setCreatedAt(dto.getCreatedAt());
        wallpaper.setUpdatedAt(dto.getUpdatedAt());
        wallpaper = wallpaperRepository.save(wallpaper);
        return convertToDto(wallpaper);
    }

//    public void deleteWallpaper(UUID id, User user) {
//        Wallpaper wallpaper = wallpaperRepository.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Wallpaper not found with id: " + id));
//
//        // Check if user is admin or the uploader
//        if (!user.getRole().equals(User.Role.ADMIN) && !wallpaper.getUploader().getId().equals(user.getId())) {
//            throw new SecurityException("Not authorized to delete this wallpaper");
//        }
//
//        // Delete from Storage
//        storageService.deleteObject(wallpaper.getFileKey());
//
//
//        wallpaperRepository.delete(wallpaper);
//    }

//    public String generateUploadUrl(String filename) {
//        String key = "wallpapers/" + UUID.randomUUID() + "/" + filename;
//        return storageService.generatePresignedUploadUrl(key);
//    }

    private WallpaperDto convertToDto(Wallpaper wallpaper) {
        WallpaperDto dto = new WallpaperDto();
        dto.setId(wallpaper.getId());
        dto.setTitle(wallpaper.getTitle());
        dto.setDescription(wallpaper.getDescription());
//        dto.setFileKey(wallpaper.getFileKey());
        dto.setPriceCents(wallpaper.getPriceCents());
        dto.setCurrency(wallpaper.getCurrency());
        dto.setIsFree(wallpaper.getIsFree());
        dto.setResolution(wallpaper.getResolution());
        dto.setFormat(wallpaper.getFormat());
//        dto.setLicenseText(wallpaper.getLicenseText());
//        dto.setTags(wallpaper.getTags());
        dto.setCreatedAt(wallpaper.getCreatedAt());
        dto.setUpdatedAt(wallpaper.getUpdatedAt());

//        if (wallpaper.getThumbnailKey() != null) {
//            dto.setThumbnailUrl(storageService.generatePresignedDownloadUrl(wallpaper.getThumbnailKey()));
//        }

        return dto;
    }
}